package org.coffee.service;

import org.coffee.annotations.Logged;
import org.coffee.persistence.dao.EmployeeDAO;
import org.coffee.persistence.dao.UserDAO;
import org.coffee.persistence.entity.Employee;
import org.coffee.persistence.entity.User;
import org.coffee.exception.RegistrationException;
import org.coffee.service.interfaces.RegistrationService;
import org.coffee.util.PasswordUtil;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityExistsException;
import javax.transaction.Transactional;

@Named
@ApplicationScoped
public class RegistrationServiceImpl implements RegistrationService {

    @Inject
    private UserDAO userDAO;

    @Inject
    private EmployeeDAO employeeDAO;

    @Override
    @Transactional
    @Logged
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
    @Logged
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
