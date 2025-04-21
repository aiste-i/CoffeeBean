package org.coffee.persistence.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.coffee.persistence.converter.BusinessHoursConverter;
import org.dhatim.businesshours.BusinessHours;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name="businesses")
public class Business implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "business_name", nullable = false)
    private String name;

    @Column(name = "street_address")
    private String streetAddress;

    @Column(name = "city")
    private String city;

    @Column(name = "postal_code")
    private String postalCode;

    @Column(name = "business_phone")
    private String phone;

    @Email
    @Column(name = "business_email")
    private String email;

    @Convert(converter = BusinessHoursConverter.class)
    @Column(name = "working_hours", columnDefinition = "TEXT")
    private BusinessHours businessHours;

    @OneToMany(mappedBy = "business", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Employee> employees = new ArrayList<>();

    @Version
    @Column(name = "opt_lock_version")
    private Integer version;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Business business = (Business) o;
        return Objects.equals(id, business.id)
                && Objects.equals(name, business.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
