package team4.shopping.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderItemService {

    @Autowired
    private OrderItemRepository orderItemRepository;

    // 新增或更新訂單明細
    @Transactional
    public OrderItem saveOrderItem(OrderItem orderItem) {
        return orderItemRepository.save(orderItem);
    }

    // 刪除訂單明細
    public boolean deleteOrderItem(OrderItemId id) {
        if (orderItemRepository.existsById(id)) {
            orderItemRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // 根據訂單 ID 查詢所有訂單明細
    public List<OrderItem> findOrderItemsByOrderId(Long orderId) {
        return orderItemRepository.findByOrderOrderId(orderId);
    }

    // 根據商品 ID 查詢包含該商品的所有訂單明細
    public List<OrderItem> findOrderItemsByProductId(int productId) {
        return orderItemRepository.findByProductProductId(productId);
    }

    // 查詢單筆訂單明細
    public Optional<OrderItem> findOrderItemById(OrderItemId id) {
        return orderItemRepository.findById(id);
    }

    // 查詢訂單總金額和商品數量
    public Map<String, Object> getOrderSummary(Long orderId) {
        List<OrderItem> orderItems = findOrderItemsByOrderId(orderId);
        
        BigDecimal totalAmount = orderItems.stream()
            .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        int totalQuantity = orderItems.stream()
            .mapToInt(OrderItem::getQuantity)
            .sum();

        return Map.of(
            "totalAmount", totalAmount,
            "totalQuantity", totalQuantity
        );
    }

    // 查詢訂單明細，包含商品名稱和金額
    public List<OrderItem> getOrderDetailsWithProductInfo(Long orderId) {
        List<OrderItem> orderItems = findOrderItemsByOrderId(orderId);

        // 計算每個商品的 subtotal 並設置到 OrderItem
        for (OrderItem item : orderItems) {
            BigDecimal subtotal = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            item.setSubtotal(subtotal); 
        }

        return orderItems;
    }

    // 根據用戶 ID 計算所有訂單的總金額
    public BigDecimal getUserTotalAmount(int userId) {
        // 查詢該用戶的所有訂單明細
        List<OrderItem> userOrderItems = orderItemRepository.findAll()
            .stream()
            .filter(orderItem -> orderItem.getOrder().getUserId() == userId)
            .collect(Collectors.toList());

        // 計算總金額
        return userOrderItems.stream()
            .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
