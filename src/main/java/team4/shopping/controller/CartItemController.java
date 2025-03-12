package team4.shopping.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team4.shopping.model.Cart;
import team4.shopping.model.CartItem;
import team4.shopping.model.CartItemId;
import team4.shopping.model.GymBean;
import team4.shopping.model.CartItemService;
import team4.shopping.model.CartRepository;
import team4.shopping.model.ShopRepository;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/cartItems")
public class CartItemController {

    @Autowired
    private CartItemService cartItemService;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ShopRepository sRepository;

    // 新增購物車明細
    @PostMapping
    public ResponseEntity<?> createCartItem(@RequestBody CartItem cartItem) {
        // 檢查是否提供 createdAt 和 updatedAt
        if (cartItem.getCreatedAt() == null || cartItem.getUpdatedAt() == null) {
            return ResponseEntity.badRequest().body("created_at 和 updated_at 為必填項！");
        }

        try {
            // 查詢 Cart 是否存在
            Optional<Cart> cart = cartRepository.findById(cartItem.getId().getCartId());
            if (cart.isEmpty()) {
                return ResponseEntity.badRequest().body("Cart not found for ID: " + cartItem.getId().getCartId());
            }
            cartItem.setCart(cart.get());

            // 查詢 Product 是否存在
            Optional<GymBean> product = sRepository.findById(cartItem.getId().getProductId());
            if (product.isEmpty()) {
                return ResponseEntity.badRequest().body("Product not found for ID: " + cartItem.getId().getProductId());
            }
            cartItem.setProduct(product.get());

            // 保存 CartItem
            CartItem savedItem = cartItemService.saveCartItem(cartItem);
            return ResponseEntity.ok(savedItem);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
        }
    }

    // 查詢購物車明細 (依據組合主鍵)
    @GetMapping("/{cartId}/{productId}")
    public ResponseEntity<?> getCartItemById(@PathVariable int cartId, @PathVariable int productId) {
        CartItemId id = new CartItemId(cartId, productId);
        Optional<CartItem> cartItem = cartItemService.findCartItemById(id);
        if (cartItem.isPresent()) {
            return ResponseEntity.ok(cartItem.get());
        }
        return ResponseEntity.status(404).body("CartItem not found with Cart ID " + cartId + " and Product ID " + productId);
    }

    // 查詢購物車的所有明細
    @GetMapping("/cart/{cartId}")
    public ResponseEntity<?> getItemsByCartId(@PathVariable int cartId) {
        try {
            // 查詢購物車明細
            List<CartItem> cartItems = cartItemService.findItemsByCartId(cartId);

            if (cartItems.isEmpty()) {
                return ResponseEntity.status(404).body("No items found for Cart ID " + cartId);
            }

            String baseUrl = "http://localhost:8082"; // 根據您的後端地址設置

            // 構建包含商品詳細資訊的返回數據
            List<Map<String, Object>> detailedItems = cartItems.stream().map(item -> {
                Map<String, Object> details = new HashMap<>();
                details.put("id", item.getId());
                details.put("quantity", item.getQuantity());
                details.put("price", item.getPrice());
                details.put("createdAt", item.getCreatedAt());
                details.put("updatedAt", item.getUpdatedAt());

                // 查詢商品詳細資訊
                Optional<GymBean> productOpt = sRepository.findById(item.getId().getProductId());
                if (productOpt.isPresent()) {
                    GymBean gymBean = productOpt.get();
                    details.put("productName", gymBean.getName());

                    // 處理圖片數據
                    if (gymBean.getImages() != null && !gymBean.getImages().isEmpty()) {
                        List<Map<String, String>> images = gymBean.getImages().stream()
                            .map(image -> Map.of("imageUrl", baseUrl + image.getImageUrl()))
                            .collect(Collectors.toList());
                        details.put("images", images);
                    } else {
                        details.put("images", Collections.emptyList()); // 無圖片時返回空列表
                    }
                } else {
                    details.put("productName", "未命名商品");
                    details.put("images", Collections.emptyList()); // 商品不存在時返回空列表
                }

                return details;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(detailedItems);
        } catch (Exception e) {
            // 捕獲異常並返回 500 狀態碼
            return ResponseEntity.status(500).body("Error retrieving items for cart ID " + cartId + ": " + e.getMessage());
        }
    }



    // 刪除購物車明細 (依據組合主鍵)
    @DeleteMapping("/{cartId}/{productId}")
    public ResponseEntity<?> deleteCartItem(@PathVariable int cartId, @PathVariable int productId) {
        CartItemId id = new CartItemId(cartId, productId);
        boolean deleted = cartItemService.deleteCartItemById(id);
        if (deleted) {
            return ResponseEntity.ok("CartItem with Cart ID " + cartId + " and Product ID " + productId + " deleted successfully.");
        }
        return ResponseEntity.status(404).body("CartItem not found with Cart ID " + cartId + " and Product ID " + productId);
    }

    // 更新購物車明細
    @PutMapping("/{cartId}/{productId}")
    public ResponseEntity<?> updateCartItem(
            @PathVariable int cartId,
            @PathVariable int productId,
            @RequestBody CartItem cartItem) {
        try {
            // 設置組合主鍵
            CartItemId id = new CartItemId(cartId, productId);
            cartItem.setId(id);

            // 查詢 Cart 是否存在
            Optional<Cart> cart = cartRepository.findById(cartId);
            if (cart.isEmpty()) {
                return ResponseEntity.badRequest().body("Cart not found for ID: " + cartId);
            }
            cartItem.setCart(cart.get());

            // 查詢 Product 是否存在
            Optional<GymBean> product = sRepository.findById(productId);
            if (product.isEmpty()) {
                return ResponseEntity.badRequest().body("Product not found for ID: " + productId);
            }
            cartItem.setProduct(product.get());

            // 更新 CartItem
            CartItem updatedItem = cartItemService.updateCartItem(cartItem);
            return ResponseEntity.ok(updatedItem);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
        }
    }
    
    //清空購物車所有商品
    @DeleteMapping("/cart/{cartId}/items")
    public ResponseEntity<?> clearCartItems(@PathVariable int cartId) {
        try {
            // 呼叫 service 清空該購物車內的所有商品
            cartItemService.deleteItemsByCartId(cartId);
            return ResponseEntity.ok("所有商品已從購物車 ID " + cartId + " 中清除");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("清空購物車內商品時發生錯誤：" + e.getMessage());
        }
    }


}
