package team4.shopping.model;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import jakarta.transaction.Transactional;

public interface CartItemRepository extends JpaRepository<CartItem, CartItemId> {

    // 查詢指定購物車的所有明細
    List<CartItem> findByCart_CartId(int cartId);
    
    @Transactional
    void deleteByCart_CartId(int cartId);
}
