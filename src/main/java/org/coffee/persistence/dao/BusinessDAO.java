package org.coffee.persistence.dao;

import org.coffee.persistence.entity.Business;
import org.coffee.persistence.entity.Employee;
import javax.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class BusinessDAO extends BaseDAO<Business> {

    protected BusinessDAO() {
        super(Business.class);
    }

    public Business addEmployee(Employee employee, Business business) {
        if(business == null || employee == null)
            throw new IllegalArgumentException("Provided employee or business is null");

        if (!employee.getBusiness().equals(business)) {
            throw new IllegalArgumentException("Employee ID is already associated with Business ID");
        }

        if (!business.equals(employee.getBusiness())) {
            employee.setBusiness(business);
        }

        if (!business.getEmployees().contains(employee)) {
            business.getEmployees().add(employee);
        }

        return update(business);
    }

}
