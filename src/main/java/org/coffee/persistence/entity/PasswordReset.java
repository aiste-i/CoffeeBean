package org.coffee.persistence.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.time.LocalDateTime;
import java.util.Objects;


@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "email_resets")
public class PasswordReset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private User user;

    @Column(name = "reset_token")
    private String token;

    @Column(name = "date_created", updatable = false)
    private LocalDateTime created;

    @Column(name = "redeemed")
    private boolean redeemed;

    @PrePersist
    protected void onCreate() {
        created = LocalDateTime.now();
        redeemed = false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PasswordReset that = (PasswordReset) o;
        return Objects.equals(id, that.id) && Objects.equals(token, that.token);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user, token);
    }
}
