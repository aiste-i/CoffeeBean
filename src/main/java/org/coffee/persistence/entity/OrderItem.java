package org.coffee.persistence.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "order_items")
public class OrderItem implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_item_name")
    private String name;

    @Column(name = "order_item_requirements")
    private String specialRequirements;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variation_id", nullable = false)
    private ProductVariation productVariation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "order_item_quantity", nullable = false)
    private Integer quantity = 1;

    @Version
    @Column(name = "opt_lock_version")
    private Integer version;

    @Column(name = "order_item_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal priceAtOrder;

    public BigDecimal getOrderItemPrice() {
        if (this.productVariation != null && this.quantity != null) {
            BigDecimal basePrice = this.productVariation.getPrice() != null ? this.productVariation.getPrice() : BigDecimal.ZERO;
            return basePrice.multiply(new BigDecimal(this.quantity));
        } else {
            return BigDecimal.ZERO;
        }
    }

    @PrePersist
    public void setPrice(){
        this.priceAtOrder = getOrderItemPrice();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderItem orderItem = (OrderItem) o;
        return Objects.equals(id, orderItem.id)
                && Objects.equals(name, orderItem.name)
                && Objects.equals(quantity, orderItem.quantity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, quantity);
    }
}
