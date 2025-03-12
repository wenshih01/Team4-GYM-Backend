package team4.shopping.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team4.shopping.model.Order;
import team4.shopping.model.OrderRepository;
import team4.shopping.utils.IdGenerator;

import java.util.List;

@Service
@Transactional
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    // 新增訂單
    public Order saveOrder(Order order) {
        // 如果訂單沒有 ID，生成一個唯一的 10 位數 ID
        if (order.getOrderId() == null) {
            Long uniqueId;
            do {
                uniqueId = IdGenerator.generateUniqueId();
            } while (orderRepository.existsByOrderId(uniqueId));
            order.setOrderId(uniqueId);
        }
        return orderRepository.save(order);
    }

    // 刪除訂單
    public void deleteOrderById(Long orderId) {
        if (!orderRepository.existsById(orderId)) {
            throw new IllegalArgumentException("Order not found with ID: " + orderId);
        }
        orderRepository.deleteById(orderId);
    }

    // 查詢單筆訂單
    public Order findOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + orderId));
    }

    // 查詢所有訂單（支持排序）
    public List<Order> findAllOrders(Sort sort) {
        return orderRepository.findAll(sort);
    }

    // 更新訂單
    public Order updateOrder(Order order) {
        Order existingOrder = findOrderById(order.getOrderId());

        // 更新變更的字段
        if (order.getUserId() != existingOrder.getUserId()) {
            existingOrder.setUserId(order.getUserId());
        }
        if (!order.getTotalPrice().equals(existingOrder.getTotalPrice())) {
            existingOrder.setTotalPrice(order.getTotalPrice());
        }
        if (!order.getStatus().equals(existingOrder.getStatus())) {
            existingOrder.setStatus(order.getStatus());
        }
        if (!order.getPaymentStatus().equals(existingOrder.getPaymentStatus())) {
            existingOrder.setPaymentStatus(order.getPaymentStatus());
        }
        if (order.getTransactionId() != null &&
            !order.getTransactionId().equals(existingOrder.getTransactionId())) {
            existingOrder.setTransactionId(order.getTransactionId());
        }
        // 新增：檢查並更新配送地址
        if (!order.getDeliveryAddress().equals(existingOrder.getDeliveryAddress())) {
            existingOrder.setDeliveryAddress(order.getDeliveryAddress());
        }

        // 更新最後修改時間
        existingOrder.setUpdatedAt(order.getUpdatedAt());

        return orderRepository.save(existingOrder);
    }

    // 查詢某用戶的訂單
    public List<Order> findOrdersByUserId(int userId, Sort sort) {
        return orderRepository.findByUserId(userId, sort);
    }

    // 查詢某用戶的特定支付狀態訂單
    public List<Order> findOrdersByUserIdAndPaymentStatus(int userId, String paymentStatus, Sort sort) {
        return orderRepository.findByUserIdAndPaymentStatus(userId, paymentStatus, sort);
    }

    // 查詢特定支付狀態的訂單
    public List<Order> findOrdersByPaymentStatus(String paymentStatus) {
        return orderRepository.findByPaymentStatus(paymentStatus);
    }

    // 根據交易編號查詢訂單
    public Order findByTransactionId(String transactionId) {
        return orderRepository.findByTransactionId(transactionId);
    }

    // 更新支付狀態
    public Order updatePaymentStatus(Long orderId, String paymentStatus) {
        // 查詢訂單
        Order order = findOrderById(orderId);

        // 更新支付狀態
        order.setPaymentStatus(paymentStatus);
        return orderRepository.save(order);
    }

    // 更新訂單狀態
    public Order updateOrderStatus(Long orderId, String status) {
        // 查詢訂單
        Order order = findOrderById(orderId);

        // 更新狀態
        order.setStatus(status);

        // 保存更新後的訂單
        return orderRepository.save(order);
    }
    
    

}
