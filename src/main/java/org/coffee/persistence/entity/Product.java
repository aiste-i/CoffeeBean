package org.coffee.persistence.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
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

    @ManyToMany(mappedBy = "products", fetch = FetchType.LAZY)
    private Set<Category> categories = new HashSet<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ProductVariation> variations = new ArrayList<>();

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

    public void removeVariation(ProductVariation size) {
        this.variations.remove(size);
        size.setProduct(null);
    }

    public void addVariation(ProductVariation size) {
        this.variations.add(size);
        size.setProduct(this);
    }

    void addCategoryInternal(Category category) {
        this.categories.add(category);
    }
    void removeCategoryInternal(Category category) {
        this.categories.remove(category);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(id, product.id)
                && Objects.equals(name, product.name)
                && Objects.equals(description, product.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description);
    }
}