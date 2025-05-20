package org.coffee.service;

import org.coffee.persistence.dao.EmployeeDAO;
import org.coffee.persistence.dao.UserDAO;
import org.coffee.persistence.entity.Employee;
import org.coffee.persistence.entity.User;
import org.coffee.dto.EmployeeAuthResult;
import org.coffee.dto.UserAuthResult;
import org.coffee.exception.AuthenticationException;
import org.coffee.service.interfaces.AuthenticationServiceInterface;
import org.coffee.util.PasswordUtil;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.Optional;

@Named
@RequestScoped
public class AuthenticationService implements AuthenticationServiceInterface {

    @Inject
    private UserDAO userDAO;

    @Inject
    private EmployeeDAO employeeDAO;

    @Override
    public UserAuthResult authenticateUser(String email, String password)
            throws AuthenticationException {
        try {
            Optional<User> optUser = userDAO.findUserByEmail(email);

            if(optUser.isPresent()) {
                User user = optUser.get();

                if (PasswordUtil.checkPassword(password, user.getPassword())) {
                    return UserAuthResult.create(true, user);
                }
            }

            return UserAuthResult.create(false, null);
        }
        catch (Exception e) {
            throw new AuthenticationException("An error occurred during user authentication.", e);
        }
    }

    @Override
    public EmployeeAuthResult authenticateEmployee(String username, String password)
            throws AuthenticationException {
        try {
            Optional<Employee> optEmployee = employeeDAO.findByUsername(username);

            if(optEmployee.isPresent()) {
                Employee employee = optEmployee.get();

                if(PasswordUtil.checkPassword(password, employee.getPassword())) {
                    return EmployeeAuthResult.create(true, employee);
                }
            }

            return EmployeeAuthResult.create(false, null);
        }
        catch (Exception e) {
            throw new AuthenticationException("An error occurred during employee authentication.", e);
        }
    }
}
