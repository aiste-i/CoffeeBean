package org.coffee.persistence.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.coffee.persistence.entity.enums.Unit;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "product_variations")
public class ProductVariation implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "variation_name", nullable = false)
    private String variationName;

    @Enumerated(EnumType.STRING)
    @Column(name = "variation_unit", nullable = false)
    private Unit unit = Unit.PIECE;

    @Min(0)
    @Max(1000)
    @Column(name = "variation_amount")
    private Integer amount;

    @Min(0)
    @Column(name = "variation_stock_quantity", nullable = false)
    private Integer stockQuantity = 0;

    @Column(name = "variation_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "variation_available", nullable = false)
    private Boolean isAvailable = true;

    @Column(name = "variation_deleted", nullable = false)
    private boolean isDeleted = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "variation_date_created", updatable = false)
    private LocalDateTime created;

    @Column(name = "variation_date_updated")
    private LocalDateTime updated;

    @Column(name = "variation_date_deleted")
    private LocalDateTime deleted;

    @Version
    @Column(name = "opt_lock_version")
    private Integer version;

    public void setAvailableOnStock(){
        isAvailable = stockQuantity > 0;
    }

    @PrePersist
    protected void onCreate() {

        created = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updated = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductVariation that = (ProductVariation) o;
        return Objects.equals(id, that.id) && Objects.equals(variationName, that.variationName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, variationName, unit, amount, stockQuantity);
    }
}