package org.coffee.service.interfaces;

import org.coffee.persistence.entity.Employee;
import org.coffee.persistence.entity.User;
import org.coffee.exception.RegistrationException;

public interface RegistrationServiceInterface {

    void registerUser(User user, String plainPassword) throws RegistrationException;

    void registerEmployee(Employee employee, String plainPassword) throws RegistrationException;
}
