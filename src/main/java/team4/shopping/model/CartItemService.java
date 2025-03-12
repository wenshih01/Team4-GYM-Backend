package team4.shopping.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CartItemService {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ShopRepository shopRepository;

    // 新增購物車明細
    public CartItem saveCartItem(CartItem cartItem) {
        // 驗證並設置 Cart
        Cart cart = validateCart(cartItem.getCart().getCartId());
        cartItem.setCart(cart);

        // 驗證並設置 Product
        GymBean product = validateProduct(cartItem.getProduct().getProductId());
        cartItem.setProduct(product);

        // 保存購物車明細
        return cartItemRepository.save(cartItem);
    }

    // 查詢購物車明細 (依據組合主鍵)
    public Optional<CartItem> findCartItemById(CartItemId cartItemId) {
        return cartItemRepository.findById(cartItemId);
    }

    // 查詢購物車的所有明細
    public List<CartItem> findItemsByCartId(int cartId) {
        return cartItemRepository.findByCart_CartId(cartId);
    }

    // 刪除購物車明細 (依據組合主鍵)
    public boolean deleteCartItemById(CartItemId cartItemId) {
        if (cartItemRepository.existsById(cartItemId)) {
            cartItemRepository.deleteById(cartItemId);
            return true;
        }
        return false;
    }

    // 更新購物車明細
    public CartItem updateCartItem(CartItem cartItem) {
        // 驗證 CartItem 是否存在
        Optional<CartItem> existingCartItemOpt = cartItemRepository.findById(cartItem.getId());
        if (!existingCartItemOpt.isPresent()) {
            throw new IllegalArgumentException("CartItem not found with ID: " + cartItem.getId());
        }

        // 驗證並設置 Cart
        Cart cart = validateCart(cartItem.getCart().getCartId());
        cartItem.setCart(cart);

        // 驗證並設置 Product
        GymBean product = validateProduct(cartItem.getProduct().getProductId());
        cartItem.setProduct(product);

        // 更新 CartItem 的欄位
        CartItem existingCartItem = existingCartItemOpt.get();
        existingCartItem.setQuantity(cartItem.getQuantity());
        existingCartItem.setPrice(cartItem.getPrice());
        existingCartItem.setUpdatedAt(cartItem.getUpdatedAt());

        // 保存並返回更新後的 CartItem
        return cartItemRepository.save(existingCartItem);
    }

    // 驗證 Cart 是否存在
    private Cart validateCart(int cartId) {
        return cartRepository.findById(cartId)
                .orElseThrow(() -> new IllegalArgumentException("Cart with ID " + cartId + " does not exist."));
    }

    // 驗證 Product 是否存在
    private GymBean validateProduct(int productId) {
        return shopRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product with ID " + productId + " does not exist."));
    }
 // 清空購物車中所有商品
    @Transactional
    public void deleteItemsByCartId(int cartId) {
        System.out.println("🛒 嘗試刪除購物車內的商品，Cart ID：" + cartId);
        
        try {
            cartItemRepository.deleteByCart_CartId(cartId);
            System.out.println("✅ 購物車商品已刪除，Cart ID：" + cartId);
        } catch (Exception e) {
            System.out.println("❌ 刪除購物車商品失敗：" + e.getMessage());
        }
    }

}
