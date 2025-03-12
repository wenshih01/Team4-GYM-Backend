package team4.shopping.model;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import team4.shopping.model.Order;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // 根據 userId 查詢某顧客的所有訂單（支持排序）
    List<Order> findByUserId(Integer userId, Sort sort);

    // 根據 userId 和訂單狀態查詢訂單
    List<Order> findByUserIdAndStatus(Integer userId, String status);

    // 根據支付狀態查詢訂單
    List<Order> findByPaymentStatus(String paymentStatus);

    // 根據 userId 和支付狀態查詢訂單（支持排序）
    List<Order> findByUserIdAndPaymentStatus(Integer userId, String paymentStatus, Sort sort);

    // 根據交易編號查詢訂單
    Order findByTransactionId(String transactionId);

    // 新增：檢查是否存在特定的 orderId
    boolean existsByOrderId(Long orderId);
}
