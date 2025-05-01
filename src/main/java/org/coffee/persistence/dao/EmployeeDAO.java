package org.coffee.persistence.dao;

import org.coffee.persistence.entity.Employee;

import javax.enterprise.context.ApplicationScoped;

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
}
