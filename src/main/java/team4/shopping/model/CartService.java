package team4.shopping.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    
    // 新增購物車
    public Cart createCart(Cart cart) {
        cart.setCreatedAt(LocalDateTime.now());
        cart.setUpdatedAt(LocalDateTime.now());
        return cartRepository.save(cart);
    }

    // 查詢購物車 (依照ID)
    public Optional<Cart> findCartById(int cartId) {
        return cartRepository.findById(cartId);
    }

    // 查詢購物車 (依照user_id)
    public Optional<Cart> findCartByUserId(int userId) {
        return cartRepository.findByUserId(userId);
    }

    // 刪除購物車
    public boolean deleteCartById(int cartId) {
        if (cartRepository.existsById(cartId)) {
            cartRepository.deleteById(cartId);
            return true;
        }
        return false;
    }

    // 更新購物車
    public Cart updateCart(Cart cart) {
        Optional<Cart> optionalCart = cartRepository.findById(cart.getCartId());
        if (optionalCart.isPresent()) {
            Cart existingCart = optionalCart.get();
            existingCart.setUpdatedAt(LocalDateTime.now()); // 更新時間為當前時間
            return cartRepository.save(existingCart);
        }
        return null; // 如果找不到該購物車，則返回 null 或者可以拋出異常
    }

    // 清空購物車
    public boolean clearCart(int cartId) {
        Optional<Cart> optionalCart = cartRepository.findById(cartId);
        if (optionalCart.isPresent()) {
            Cart cart = optionalCart.get();
            cart.getCartItems().clear(); // 清空購物車商品
            cart.setUpdatedAt(LocalDateTime.now()); // 更新清空操作的時間
            cartRepository.save(cart); // 保存修改後的購物車
            return true;
        }
        return false;
    }
       
}
