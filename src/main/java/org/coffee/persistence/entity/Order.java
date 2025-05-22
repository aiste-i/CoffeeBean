package org.coffee.persistence.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.coffee.persistence.entity.enums.OrderStatus;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.*;
import javax.validation.constraints.Email;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "orders")
public class Order implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_name", nullable = false)
    private String name = null;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<OrderItem> items = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonbTransient
    private User user;

    @Column(name = "customer_name", length = 100)
    private String customerName;

    @Email
    @Column(name = "customer_email")
    private String customerEmail;

    @Column(name = "total_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice = BigDecimal.ZERO;

    @Column(name = "stripe_checkout_session_id", unique = true)
    private String stripeCheckoutSessionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false)
    private OrderStatus orderStatus = OrderStatus.PENDING;

    @OneToMany( mappedBy = "order", fetch = FetchType.LAZY)
    @JsonbTransient
    private List<Payment> payments = new ArrayList<>();

    @Column(name = "order_date_created", updatable = false)
    private LocalDateTime created;

    @Column(name = "order_date_updated")
    private LocalDateTime updated;

    @Column(name = "order_date_completed")
    private LocalDateTime completed;

    @Version
    @Column(name = "opt_lock_version")
    @JsonbTransient
    private Integer version;

    public void addItem(OrderItem item) {
        this.items.add(item);
        item.setOrder(this);
    }

    public void removeItem(OrderItem item) {
        this.items.remove(item);
        item.setOrder(null);
    }

    public BigDecimal calculateTotalPrice() {
        return this.items.stream()
                .map(OrderItem::calculatePrice)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @PrePersist
    protected void onCreate() {
        totalPrice = calculateTotalPrice();
        created = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        totalPrice = calculateTotalPrice();
        updated = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(id, order.id)
                && Objects.equals(name, order.name)
                && Objects.equals(totalPrice, order.totalPrice);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, totalPrice);
    }
}

