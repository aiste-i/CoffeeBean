package org.coffee.dto;

import lombok.Getter;
import org.coffee.persistence.entity.Employee;
import org.coffee.persistence.entity.User;

public class EmployeeAuthResult extends AuthResult {

    public EmployeeAuthResult(boolean success, Employee admin){
        super(success, admin);
    }

    @Override
    public Employee getUser() {
        return (Employee) super.getUser();
    }
}
