package team4.shopping.model;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, OrderItemId> {

    // 根據訂單 ID 查詢所有訂單明細
    List<OrderItem> findByOrderOrderId(Long orderId);

    // 根據商品 ID 查詢包含該商品的所有訂單明細
    List<OrderItem> findByProductProductId(int productId);
}
