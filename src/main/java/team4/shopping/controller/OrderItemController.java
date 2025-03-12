package team4.shopping.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team4.shopping.model.OrderItem;
import team4.shopping.model.OrderItemId;
import team4.shopping.model.OrderItemService;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orderitems")
public class OrderItemController {

    @Autowired
    private OrderItemService orderItemService;

    // 新增或更新訂單明細
    @PostMapping
    public ResponseEntity<OrderItem> createOrderItem(@RequestBody OrderItem orderItem) {
        if (orderItem.getOrder() == null || orderItem.getProduct() == null) {
            return ResponseEntity.badRequest().body(null);
        }

        try {
            OrderItem savedOrderItem = orderItemService.saveOrderItem(orderItem);
            return ResponseEntity.ok(savedOrderItem);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // 根據訂單 ID 查詢所有訂單明細
    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<OrderItem>> getOrderItemsByOrderId(@PathVariable Long orderId) {
        List<OrderItem> orderItems = orderItemService.findOrderItemsByOrderId(orderId);
        if (orderItems.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(orderItems);
    }

    // 根據商品 ID 查詢包含該商品的所有訂單明細
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<OrderItem>> getOrderItemsByProductId(@PathVariable int productId) {
        List<OrderItem> orderItems = orderItemService.findOrderItemsByProductId(productId);
        if (orderItems.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(orderItems);
    }

    // 查詢單筆訂單明細
    @GetMapping("/order/{orderId}/product/{productId}")
    public ResponseEntity<OrderItem> getOrderItemById(@PathVariable Long orderId, @PathVariable int productId) {
        // 校驗參數
        if (orderId <= 0 || productId <= 0) {
            return ResponseEntity.badRequest().body(null); // 返回 400 錯誤
        }

        OrderItemId id = new OrderItemId(orderId, productId);
        Optional<OrderItem> orderItem = orderItemService.findOrderItemById(id);
        return orderItem.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // 查詢訂單總金額和商品數量
    @GetMapping("/order/{orderId}/summary")
    public ResponseEntity<Map<String, Object>> getOrderSummary(@PathVariable Long orderId) {
        Map<String, Object> summary = orderItemService.getOrderSummary(orderId);
        if (summary == null || summary.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(summary);
    }

    // 查詢用戶所有訂單的總金額
    @GetMapping("/user/{userId}/total-amount")
    public ResponseEntity<BigDecimal> getUserTotalAmount(@PathVariable int userId) {
        BigDecimal totalAmount = orderItemService.getUserTotalAmount(userId);
        if (totalAmount.equals(BigDecimal.ZERO)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(totalAmount);
    }

    // 查詢訂單明細，包含商品名稱和金額
    @GetMapping("/order/{orderId}/details")
    public ResponseEntity<List<Map<String, Object>>> getOrderDetailsWithProductInfo(@PathVariable Long orderId) {
        List<OrderItem> details = orderItemService.getOrderDetailsWithProductInfo(orderId);

        if (details == null || details.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // 轉換 OrderItem 資料，確保包含 productName 和 imageUrl
        List<Map<String, Object>> responseList = details.stream().map(item -> {
            Map<String, Object> map = new HashMap<>();
            map.put("orderId", item.getId().getOrderId());
            map.put("productId", item.getId().getProductId());
            map.put("productName", item.getProduct() != null ? item.getProduct().getName() : "未知商品");
            map.put("quantity", item.getQuantity());
            map.put("price", item.getPrice());
            map.put("subtotal", item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));

            // ✅ 確保返回商品圖片，只取第一張
            if (item.getProduct() != null && item.getProduct().getImages() != null && !item.getProduct().getImages().isEmpty()) {
                map.put("imageUrl", item.getProduct().getImages().get(0).getImageUrl()); // 只取第一張
            } else {
                map.put("imageUrl", "/no-image.png"); // 如果沒有圖片，使用預設圖片
            }

            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(responseList);
    }




    // 刪除訂單明細
    @DeleteMapping("/order/{orderId}/product/{productId}")
    public ResponseEntity<Void> deleteOrderItem(@PathVariable Long orderId, @PathVariable int productId) {
        OrderItemId id = new OrderItemId(orderId, productId);
        boolean deleted = orderItemService.deleteOrderItem(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
