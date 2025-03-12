package team4.shopping.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(name = "CartItems")
@Component
public class CartItem implements Serializable {

    @EmbeddedId
    private CartItemId id; // 嵌套主鍵

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("cartId") // 將嵌套主鍵中的 cartId 映射為外鍵
    @JoinColumn(name = "cart_id", nullable = false)
    @JsonBackReference(value = "cart-cartitem")
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("productId") // 將嵌套主鍵中的 productId 映射為外鍵
    @JoinColumn(name = "product_id", nullable = false)
    @JsonBackReference(value = "product-cartitem")
    private GymBean product;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "price", nullable = false)
    private String price;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public CartItemId getId() {
        return id;
    }

    public void setId(CartItemId id) {
        this.id = id;
    }

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public GymBean getProduct() {
        return product;
    }

    public void setProduct(GymBean product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
