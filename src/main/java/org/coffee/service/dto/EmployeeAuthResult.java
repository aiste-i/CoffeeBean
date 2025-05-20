package org.coffee.service.dto;

import lombok.Getter;
import org.coffee.persistence.entity.Employee;

public class EmployeeAuthResult {

    @Getter
    private final boolean success;

    @Getter
    private final Employee employee;

    private EmployeeAuthResult(boolean success, Employee admin){
        this.success = success;
        this.employee = admin;
    }

    public static EmployeeAuthResult create(boolean success, Employee employee) {
        return new EmployeeAuthResult(success, employee);
    }
}
