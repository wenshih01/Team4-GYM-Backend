package team4.shopping.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team4.shopping.model.Order;
import team4.shopping.model.OrderService;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    // 新增訂單
    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        // 確保配送地址非空
        if (order.getDeliveryAddress() == null || order.getDeliveryAddress().isEmpty()) {
            return ResponseEntity.badRequest().body(null); // 回傳 400 錯誤
        }
        Order savedOrder = orderService.saveOrder(order);
        return ResponseEntity.ok(savedOrder);
    }

    // 查詢單筆訂單
    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long orderId) {
        Order order = orderService.findOrderById(orderId);
        return ResponseEntity.ok(order);
    }

    // 查詢所有訂單（支持排序）
    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders(
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        List<Order> orders = orderService.findAllOrders(sort);
        return ResponseEntity.ok(orders);
    }

    // 刪除訂單
    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long orderId) {
        orderService.deleteOrderById(orderId);
        return ResponseEntity.noContent().build();
    }

    // 更新訂單
    @PutMapping("/{orderId}")
    public ResponseEntity<Order> updateOrder(@PathVariable Long orderId, @RequestBody Order order) {
        // 確保配送地址非空
        if (order.getDeliveryAddress() == null || order.getDeliveryAddress().isEmpty()) {
            return ResponseEntity.badRequest().body(null); // 回傳 400 錯誤
        }
        order.setOrderId(orderId);
        Order updatedOrder = orderService.updateOrder(order);
        return ResponseEntity.ok(updatedOrder);
    }

    // 查詢某用戶的所有訂單（支持排序）
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Order>> getOrdersByUserId(
            @PathVariable int userId,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        List<Order> orders = orderService.findOrdersByUserId(userId, sort);
        return ResponseEntity.ok(orders);
    }

    // 查詢某用戶的特定支付狀態訂單（支持排序）
    @GetMapping("/user/{userId}/payment-status/{paymentStatus}")
    public ResponseEntity<List<Order>> getOrdersByUserIdAndPaymentStatus(
            @PathVariable int userId,
            @PathVariable String paymentStatus,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        List<Order> orders = orderService.findOrdersByUserIdAndPaymentStatus(userId, paymentStatus, sort);
        return ResponseEntity.ok(orders);
    }

    // 查詢特定支付狀態的訂單
    @GetMapping("/payment-status/{paymentStatus}")
    public ResponseEntity<List<Order>> getOrdersByPaymentStatus(@PathVariable String paymentStatus) {
        List<Order> orders = orderService.findOrdersByPaymentStatus(paymentStatus);
        return ResponseEntity.ok(orders);
    }

    // 查詢交易編號對應的訂單
    @GetMapping("/transaction/{transactionId}")
    public ResponseEntity<Order> getOrderByTransactionId(@PathVariable String transactionId) {
        Order order = orderService.findByTransactionId(transactionId);
        return ResponseEntity.ok(order);
    }

    // 更新支付狀態
    @PatchMapping("/{orderId}/payment-status")
    public ResponseEntity<Order> updatePaymentStatus(
            @PathVariable Long orderId,
            @RequestParam String paymentStatus) {
        // 確保 paymentStatus 是合法的值
        if (!paymentStatus.equalsIgnoreCase("Pending") &&
            !paymentStatus.equalsIgnoreCase("Completed") &&
            !paymentStatus.equalsIgnoreCase("Failed")) {
            return ResponseEntity.badRequest().body(null); // 回傳 400 錯誤
        }

        // 更新訂單支付狀態
        Order updatedOrder = orderService.updatePaymentStatus(orderId, paymentStatus);
        return ResponseEntity.ok(updatedOrder);
    }

    // 更新訂單狀態
    @PatchMapping("/{orderId}/status")
    public ResponseEntity<Order> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam String status) {
        // 驗證狀態是否合法
        if (!status.equalsIgnoreCase("Pending") &&
            !status.equalsIgnoreCase("Completed") &&
            !status.equalsIgnoreCase("Cancelled")) {
            return ResponseEntity.badRequest().body(null); // 回傳 400 錯誤
        }

        // 更新訂單狀態
        Order updatedOrder = orderService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok(updatedOrder);
    }
}
