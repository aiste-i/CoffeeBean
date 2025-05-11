package org.coffee.persistence.dao;

import org.coffee.persistence.entity.Employee;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import java.util.Optional;

@ApplicationScoped
public class EmployeeDAO extends BaseDAO<Employee> {
    protected EmployeeDAO() {
        super(Employee.class);
    }

    public Optional<Employee> findByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return Optional.empty();
        }
        try {
            Employee employee = em.createQuery(
                            "SELECT e FROM Employee e WHERE e.username = :username", Employee.class)
                    .setParameter("username", username)
                    .getSingleResult();
            return Optional.of(employee);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public Optional<Employee> findByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return Optional.empty();
        }
        try {
            Employee employee = em.createQuery(
                            "SELECT e FROM Employee e WHERE e.email = :email", Employee.class)
                    .setParameter("email", email)
                    .getSingleResult();
            return Optional.of(employee);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public boolean removeByUsername(String username) {
        try {
            Employee employee = this.findByUsername(username).orElse(null);

            if (employee != null) {
                em.remove(employee);
                return true;
            } else {
                return false;
            }
        } catch (NoResultException e) {
            return false;
        } catch (Exception e) {
            throw new RuntimeException("Failed to remove employee (safe) " + username, e);
        }
    }
}
