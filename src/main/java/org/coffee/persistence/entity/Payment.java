package org.coffee.persistence.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.coffee.persistence.entity.enums.Currency;
import org.coffee.persistence.entity.enums.PaymentStatus;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "payments")
public class Payment implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency", nullable = false, length = 3)
    private Currency currency = Currency.EUR;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PaymentStatus status = PaymentStatus.PENDING;

    @Column(name = "payment_method_type", length = 50) // e.g., "card", "ideal", "sepa_debit" from Stripe
    private String paymentMethodType;

    @Column(name = "failure_code", length = 100) // e.g., card_declined (from Stripe error)
    private String failureCode;

    @Column(name = "failure_message", length = 500) // Message from Stripe error
    private String failureMessage;

    @Column(name = "gateway", length = 50, nullable = false)
    private String gateway = "Stripe";

    @Column(name = "gateway_payment_intent_id", unique = true)
    private String gatewayPaymentIntentId;

    @Column(name = "gateway_charge_id", unique = true)
    private String gatewayChargeId;

    @Column(name = "payment_date_created", nullable = false, updatable = false)
    private LocalDateTime created;

    @Column(name = "payment_date_updated")
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Payment payment = (Payment) o;
        return Objects.equals(id, payment.id)
                && Objects.equals(order, payment.order)
                && Objects.equals(amount, payment.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, amount, created);
    }
}