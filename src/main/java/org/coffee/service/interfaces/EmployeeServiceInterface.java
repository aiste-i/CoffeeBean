package org.coffee.service.interfaces;

import org.coffee.persistence.entity.Employee;
import org.coffee.service.exceptions.CredentialChangeException;

import javax.persistence.OptimisticLockException;

public interface EmployeeServiceInterface {

    Employee getEmployeeById(Long id);

    boolean changePassword(Employee employee, String oldPassword, String password)
            throws CredentialChangeException, OptimisticLockException;
}
