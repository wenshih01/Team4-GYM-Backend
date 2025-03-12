package team4.shopping.model;

import org.springframework.stereotype.Component;
import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "OrderItems")
@Component
public class OrderItem implements Serializable {

    @EmbeddedId
    private OrderItemId id; // 嵌套主鍵

    @ManyToOne
    @MapsId("orderId") // 將嵌套主鍵中的 orderId 映射為外鍵
    @JoinColumn(name = "order_id", nullable = false)
    @JsonBackReference(value = "order-orderitem")
    private Order order;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("productId") // 將嵌套主鍵中的 productId 映射為外鍵
    @JoinColumn(name = "product_id", nullable = false)
    @JsonBackReference(value = "product-orderitem")
    private GymBean product;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Transient // 非持久化屬性  設置臨時屬性 subtotal
    private BigDecimal subtotal;
    
    
    
    
    public OrderItemId getId() {
        return id;
    }

    public void setId(OrderItemId id) {
        this.id = id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
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

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    
    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
}
