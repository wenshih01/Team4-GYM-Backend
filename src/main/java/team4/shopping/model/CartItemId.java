package team4.shopping.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class CartItemId implements Serializable {

    @Column(name = "cart_id")
    private int cartId;

    @Column(name = "product_id")
    private int productId;

    
    public CartItemId() {
    }

    public CartItemId(int cartId, int productId) {
        this.cartId = cartId;
        this.productId = productId;
    }

   
    public int getCartId() {
        return cartId;
    }

    public void setCartId(int cartId) {
        this.cartId = cartId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    // equals 和 hashCode（必須實現，用於比較主鍵）
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CartItemId that = (CartItemId) o;
        return cartId == that.cartId && productId == that.productId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(cartId, productId);
    }
}
