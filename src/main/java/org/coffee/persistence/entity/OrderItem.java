package org.coffee.persistence.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "order_items")
public class OrderItem implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "requirements")
    private String specialRequirements;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @JsonbTransient
    private Order order;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "order_item_addons",
            joinColumns = @JoinColumn(name = "order_item_id"),
            inverseJoinColumns = @JoinColumn(name = "addon_ingredient_id")
    )
    private Set<Ingredient> addons = new HashSet<>();

    @Column(name = "quantity", nullable = false)
    private Integer quantity = 1;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal priceAtOrder;

    @Version
    @Column(name = "opt_lock_version")
    @JsonbTransient
    private Integer version;

    public BigDecimal calculateUnitPrice() {
        if (this.product != null) {
            BigDecimal basePrice = this.product.getPrice() != null ? this.product.getPrice() : BigDecimal.ZERO;

            BigDecimal addonPrice = addons.stream()
                    .map(Ingredient::getPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            return basePrice.add(addonPrice);
        } else {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal calculatePrice() {
        if (this.product != null && this.quantity != null) {
            BigDecimal unitPrice = calculateUnitPrice();
            return unitPrice.multiply(new BigDecimal(this.quantity));
        } else {
            return BigDecimal.ZERO;
        }
    }

    public String getAddonNames() {
        return addons.stream()
                .map(Ingredient::getName)
                .collect(Collectors.joining(", "));
    }

    @PrePersist
    private void setPrice(){
        this.priceAtOrder = calculatePrice();
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
