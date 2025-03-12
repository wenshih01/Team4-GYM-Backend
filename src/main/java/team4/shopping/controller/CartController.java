package team4.shopping.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team4.shopping.model.Cart;
import team4.shopping.model.CartService;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    // 創建新的購物車
    @PostMapping
    public ResponseEntity<?> createCart(@RequestBody Cart cart) {
        if (cart.getCreatedAt() == null) {
            cart.setCreatedAt(LocalDateTime.now()); // 自動設置創建時間
        }
        if (cart.getUpdatedAt() == null) {
            cart.setUpdatedAt(LocalDateTime.now()); // 自動設置更新時間
        }

        Cart createdCart = cartService.createCart(cart);
        return ResponseEntity.ok(createdCart);
    }

    // 根據 cartId 查詢購物車
    @GetMapping("/{cartId}")
    public ResponseEntity<?> getCartById(@PathVariable("cartId") int cartId) {
        Optional<Cart> cart = cartService.findCartById(cartId);
        if (cart.isPresent()) {
            return ResponseEntity.ok(cart.get());
        }
        return ResponseEntity.status(404).body("Cart not found with ID " + cartId);
    }

    // 根據 userId 查詢購物車
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getCartByUserId(@PathVariable("userId") int userId) {
        Optional<Cart> cart = cartService.findCartByUserId(userId);
        if (cart.isPresent()) {
            return ResponseEntity.ok(cart.get());
        }
        return ResponseEntity.status(404).body("Cart not found for user with ID " + userId);
    }

    // 更新購物車
    @PutMapping("/{cartId}")
    public ResponseEntity<?> updateCart(@PathVariable("cartId") int cartId, @RequestBody Cart cart) {
        cart.setCartId(cartId); // 確保 cartId 與 URL 一致
        cart.setUpdatedAt(LocalDateTime.now()); // 自動更新時間
        Cart updatedCart = cartService.updateCart(cart);
        if (updatedCart != null) {
            return ResponseEntity.ok(updatedCart);
        }
        return ResponseEntity.status(404).body("Cart not found with ID " + cartId);
    }

    // 刪除購物車
    @DeleteMapping("/{cartId}")
    public ResponseEntity<?> deleteCart(@PathVariable("cartId") int cartId) {
        boolean deleted = cartService.deleteCartById(cartId);
        if (deleted) {
            return ResponseEntity.ok("Cart with ID " + cartId + " deleted successfully.");
        }
        return ResponseEntity.status(404).body("Cart not found with ID " + cartId);
    }

    // 清空購物車
    @DeleteMapping("/{cartId}/clear")
    public ResponseEntity<?> clearCart(@PathVariable("cartId") int cartId) {
        boolean cleared = cartService.clearCart(cartId);
        if (cleared) {
            return ResponseEntity.ok("Cart with ID " + cartId + " cleared successfully.");
        }
        return ResponseEntity.status(404).body("Cart not found with ID " + cartId);
    }
    

}
