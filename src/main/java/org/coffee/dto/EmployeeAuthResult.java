package org.coffee.dto;

import org.coffee.persistence.entity.Employee;

public class EmployeeAuthResult extends AuthResult {

    public EmployeeAuthResult(boolean success, Employee admin) {
        super(success, admin);
    }

    @Override
    public Employee getUser() {
        return (Employee) super.getUser();
    }
}