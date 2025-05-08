package org.coffee.persistence.dao;

import org.coffee.persistence.entity.Employee;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.NoResultException;
import javax.transaction.Transactional;

@ApplicationScoped
public class EmployeeDAO extends BaseDAO<Employee> {
    protected EmployeeDAO() {
        super(Employee.class);
    }

    public Employee findByUsername(String username) {
        return em.createQuery(
                        "SELECT e FROM Employee e WHERE e.username = :username",
                        Employee.class)
                .setParameter("username", username)
                .getSingleResult();
    }

    public Employee findByEmail(String email) {
        return em.createQuery(
                        "SELECT e FROM Employee e WHERE e.email = :email",
                        Employee.class)
                .setParameter("email", email)
                .getSingleResult();
    }

    public Employee findById(Long id) {
        return em.find(Employee.class, id);
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public boolean removeByUsername(String username) {
        try {
            Employee employee = this.findByUsername(username);
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

    @Transactional
    public void merge(Employee employee) {
        em.merge(employee);
    }
}
