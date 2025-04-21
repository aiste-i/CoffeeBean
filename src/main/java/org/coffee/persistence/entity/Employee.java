package org.coffee.persistence.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.coffee.persistence.entity.enums.EmployeeRole;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "employees")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employee_firstname", nullable = false)
    private String firstName;

    @Column(name = "employee_lastname", nullable = false)
    private String lastName;

    @Column(name = "employee_username", nullable = false, unique = true)
    private String username;

    @Column(name = "employee_password", nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "employee_role", nullable = false)
    private EmployeeRole role;

    @Column(name = "deleted", nullable = false)
    private boolean isDeleted;

    @Column(name = "date_created", updatable = false)
    private LocalDateTime created;

    @Column(name = "date_updated")
    private LocalDateTime updated;

    @Column(name = "date_deleted")
    private LocalDateTime deleted;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "business_id", nullable = false)
    private Business business;

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
        Employee employee = (Employee) o;
        return Objects.equals(id, employee.id)
                && Objects.equals(username, employee.username)
                && Objects.equals(created, employee.created);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, created);
    }
}
