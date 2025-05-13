package org.coffee.persistence.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "products")
public class Product implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_name", nullable = false)
    private String name;

    @Column(name = "product_description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private ProductCategory category;

    @Column(name = "price",nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "product_date_created", updatable = false)
    private LocalDateTime created;

    @Column(name = "product_date_updated")
    private LocalDateTime updated;

    @Version
    @Column(name = "opt_lock_version")
    private Integer version;

    @PrePersist
    protected void onCreate() {
        created = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updated = LocalDateTime.now();
    }

    public List<IngredientType> getValidAddonIngredientTypes(){
        if(category == null) {
            return null;
        }

        List<IngredientType> validTypes = category.getAddonIngredientTypes();
        if(validTypes != null) {
            return category.getAddonIngredientTypes();
        }

        return new ArrayList<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(id, product.id)
                && Objects.equals(name, product.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description);
    }
}