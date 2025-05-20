package org.coffee.service;

import org.coffee.persistence.dao.EmployeeDAO;
import org.coffee.persistence.dao.UserDAO;
import org.coffee.persistence.entity.Employee;
import org.coffee.persistence.entity.User;
import org.coffee.service.exceptions.RegistrationException;
import org.coffee.service.interfaces.RegistrationServiceInterface;
import org.coffee.util.PasswordUtil;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityExistsException;
import javax.transaction.Transactional;

@Named
@RequestScoped
public class RegistrationService implements RegistrationServiceInterface {

    @Inject
    private UserDAO userDAO;

    @Inject
    private EmployeeDAO employeeDAO;

    @Override
    @Transactional
    public void registerUser(User user, String plainPassword) throws RegistrationException {
        try {
            user.setPassword(PasswordUtil.hashPassword(plainPassword));

            userDAO.persist(user);
        }
        catch (EntityExistsException e) {
            throw new RegistrationException("User already exists.", e);
        }
        catch (Exception e) {
            throw new RegistrationException("An unexpected error occurred during user registration.", e);
        }
    }

    @Override
    @Transactional
    public void registerEmployee(Employee employee, String plainPassword) throws RegistrationException {
        try {
            employee.setPassword(PasswordUtil.hashPassword(plainPassword));

            employeeDAO.persist(employee);
        }
        catch (EntityExistsException e) {
            throw new RegistrationException("Employee already exists.", e);
        }
        catch (Exception e) {
            throw new RegistrationException("An unexpected error occurred during user registration.", e);
        }
    }
}
