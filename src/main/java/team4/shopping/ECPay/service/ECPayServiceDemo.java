package team4.shopping.ECPay.service;

import ecpay.payment.integration.AllInOne;
import ecpay.payment.integration.domain.AioCheckOutALL;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import team4.shopping.model.Cart;
import team4.shopping.model.CartItem;
import team4.shopping.model.CartItemService;
import team4.shopping.model.CartRepository;
import team4.shopping.model.Order;
import team4.shopping.model.OrderItem;
import team4.shopping.model.OrderItemId;
import team4.shopping.model.OrderItemService;
import team4.shopping.model.OrderService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ECPayServiceDemo {

    private final OrderService orderService;
    
    @Autowired
    private CartRepository cartRepository;
    
    @Autowired
    private CartItemService cartItemService;


    @Autowired
    private OrderItemService orderItemService;

    public ECPayServiceDemo(OrderService orderService) {
        this.orderService = orderService;
    }

    @Value("${ecpay.return.url}")
    private String returnUrl;

    @Value("${ecpay.client.back.url}")
    private String clientBackUrl;

    //  生成 ECPay 付款表單
    public String ecpayCheckout(int userId, int totalAmount, String deliveryAddress) {
        System.out.println("✅ ECPay 結帳請求: userId=" + userId + ", totalAmount=" + totalAmount + ", deliveryAddress=" + deliveryAddress);

        //  產生 ECPay 訂單編號
        String merchantTradeNo = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 20);

        //  取得當前時間
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        String merchantTradeDate = LocalDateTime.now().format(formatter);

        //  建立訂單，透過 `OrderService` 來產生 `orderId`
        Order order = new Order();
        order.setUserId(userId);
        order.setTotalPrice(String.valueOf(totalAmount)); //  轉換 `int` 為 `String`
        order.setDeliveryAddress(deliveryAddress);
        order.setStatus("Pending");
        order.setPaymentStatus("Pending");
        order.setTransactionId(merchantTradeNo);

        //  `OrderService` 生成 `orderId` 並儲存訂單
        Order savedOrder = orderService.saveOrder(order);
        System.out.println("✅ 訂單已存入，Order ID: " + savedOrder.getOrderId() + ", Transaction ID: " + merchantTradeNo);
        
        
        
        
        //  設定 ECPay 付款
        AllInOne all = new AllInOne("");

        AioCheckOutALL obj = new AioCheckOutALL();
        obj.setMerchantTradeNo(merchantTradeNo); // 設定交易編號
        obj.setMerchantTradeDate(merchantTradeDate);
        obj.setTotalAmount(String.valueOf(totalAmount)); //  確保傳入 `int` 金額
        obj.setTradeDesc("購物清單");
        obj.setItemName("購物商品");
        obj.setReturnURL(returnUrl);
        obj.setClientBackURL(clientBackUrl);
        obj.setNeedExtraPaidInfo("N");

        //  產生 ECPay 付款表單
        String form = all.aioCheckOut(obj, null);

        return form;  //  回傳 HTML 給前端，讓用戶跳轉到 ECPay 付款
    }

    public Order findOrderByTransactionId(String transactionId) {
        Order order = orderService.findByTransactionId(transactionId);
        System.out.println("📌 查詢 Transaction ID [" + transactionId + "]，找到的 Order: " + order);
        return order;
    }

    
    
 // 更新訂單狀態，並存入 OrderItem
    public boolean updateOrderStatus(String merchantTradeNo, String paymentStatus, String orderStatus) {
        Order order = findOrderByTransactionId(merchantTradeNo);
        if (order != null) {
            order.setPaymentStatus(paymentStatus);
            order.setStatus(orderStatus);
            orderService.updateOrder(order); // ✅ 儲存變更
            return true; // ✅ 修改成功回傳 true
        }
        return false; // ✅ 找不到訂單則回傳 false
    }

    
    @Transactional
    public void saveOrderItems(Long orderId, int userId) {
        System.out.println("📌 嘗試存入 OrderItem，Order ID: " + orderId + ", User ID: " + userId);

        // 1️⃣ 查找購物車
        Optional<Cart> optionalCart = cartRepository.findByUserId(userId);
        if (optionalCart.isEmpty()) {
            System.out.println("❌ 找不到對應的購物車，無法存入 OrderItem");
            return;
        }
        Cart cart = optionalCart.get();
        System.out.println("📌 找到購物車，Cart ID: " + cart.getCartId());

        // 2️⃣ 查找購物車商品 (CartItem)
        List<CartItem> cartItems = cartItemService.findItemsByCartId(cart.getCartId());
        if (cartItems == null || cartItems.isEmpty()) {
            System.out.println("❌ 沒有找到購物車商品，無法存入 OrderItem");
            return;
        }
        System.out.println("📌 找到 " + cartItems.size() + " 個購物車商品，開始轉存 OrderItem");

        // 3️⃣ 查找對應的訂單 (Order)
        Order order = orderService.findOrderById(orderId);
        if (order == null) {
            System.out.println("❌ 找不到對應的訂單，無法存入 OrderItem");
            return;
        }
        System.out.println("📌 確認 Order ID: " + order.getOrderId()); // 確保 orderId 有值

        // 4️⃣ 逐筆存入 OrderItem
        for (CartItem item : cartItems) {
            try {
                int productId = item.getProduct().getProductId(); // 確保 productId 存在
                OrderItemId orderItemId = new OrderItemId(order.getOrderId(), productId);

                OrderItem orderItem = new OrderItem();
                orderItem.setId(orderItemId);
                orderItem.setOrder(order);
                orderItem.setProduct(item.getProduct());
                orderItem.setQuantity(item.getQuantity());
                orderItem.setPrice(BigDecimal.valueOf(Double.parseDouble(item.getPrice())));

                // 5️⃣ 存入 OrderItem
                orderItemService.saveOrderItem(orderItem);
                System.out.println("✅ 訂單明細存入成功，商品名稱：" + item.getProduct().getName());

            } catch (Exception e) {
                System.out.println("❌ 訂單明細存入失敗：" + e.getMessage());
            }
        }

        System.out.println("✅ 所有訂單明細存入成功！");
    }









}
