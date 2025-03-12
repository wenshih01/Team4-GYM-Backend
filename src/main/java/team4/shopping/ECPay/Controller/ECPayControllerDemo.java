package team4.shopping.ECPay.Controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import team4.shopping.ECPay.service.ECPayServiceDemo;
import team4.shopping.model.Order;


@RestController
@RequestMapping("/api/ecpay")
public class ECPayControllerDemo {

    private final ECPayServiceDemo ecpayService;

    public ECPayControllerDemo(ECPayServiceDemo ecpayService) {
        this.ecpayService = ecpayService;
    }

    //前端請求 ECPay 結帳（生成付款表單）
    @PostMapping("/checkout")
    public ResponseEntity<String> ecpayCheckout(@RequestBody Map<String, Object> payload) {
        try {
            int userId = Integer.parseInt(payload.get("userId").toString()); //  確保 userId 是 int
            double totalPrice = Double.parseDouble(payload.get("totalPrice").toString()); //  確保 totalPrice 是 double
            String deliveryAddress = (String) payload.get("deliveryAddress");

            System.out.println(" 收到 ECPay 付款請求: userId=" + userId + ", totalPrice=" + totalPrice + ", deliveryAddress=" + deliveryAddress);

            //  確保傳入的金額是整數（ECPay 不允許小數點）
            int totalAmount = (int) Math.round(totalPrice);

            //  調用 ECPayService 進行付款
            String paymentForm = ecpayService.ecpayCheckout(userId, totalAmount, deliveryAddress);

            return ResponseEntity.ok(paymentForm);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(" 錯誤: " + e.getMessage());
        }
    }



    //ECPay 付款結果回傳（付款成功後 ECPay 自動呼叫此 API）
    @PostMapping("/return")
    public ResponseEntity<String> handlePaymentReturn(@RequestParam("MerchantTradeNo") String merchantTradeNo,
                                                      @RequestParam("RtnCode") int rtnCode,
                                                      @RequestParam("RtnMsg") String rtnMsg) {
        System.out.println("📌 收到 ECPay 回傳，MerchantTradeNo: " + merchantTradeNo + ", RtnCode: " + rtnCode);

        //確保能找到對應的訂單
        Order order = ecpayService.findOrderByTransactionId(merchantTradeNo);
        if (order == null) {
            System.out.println("❌ 找不到 Transaction ID [" + merchantTradeNo + "] 的訂單，無法處理付款");
            return ResponseEntity.status(404).body("❌ 找不到訂單");
        }

        System.out.println("📌 找到訂單，Order ID: " + order.getOrderId() + ", User ID: " + order.getUserId());

        //  判斷付款狀態
        if (rtnCode == 1) { // **1 = 付款成功**
            System.out.println("✅ 付款成功，更新訂單狀態中...");
            
            boolean isUpdated = ecpayService.updateOrderStatus(merchantTradeNo, "Completed", "Pending");
            if (!isUpdated) {
                System.out.println("❌ 訂單狀態更新失敗！");
                return ResponseEntity.status(500).body("❌ 訂單狀態更新失敗");
            }

            //  確保 `OrderItem` 存入
            System.out.println("📌 開始存入 OrderItem...");
            ecpayService.saveOrderItems(order.getOrderId(), order.getUserId());

            System.out.println("✅ 付款成功，訂單與訂單明細已完成");
            return ResponseEntity.ok("✅ 付款成功，訂單已完成！");
        } else {
            System.out.println("❌ 付款失敗，錯誤訊息：" + rtnMsg);
            return ResponseEntity.status(400).body("❌ 付款失敗: " + rtnMsg);
        }
    }








}
