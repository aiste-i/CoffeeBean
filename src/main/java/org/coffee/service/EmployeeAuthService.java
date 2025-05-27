package org.coffee.service;

import org.coffee.annotations.EmployeeAuth;
import org.coffee.annotations.Logged;
import org.coffee.dto.AuthResult;
import org.coffee.exception.AuthenticationException;
import org.coffee.factory.AuthResultFactory;
import org.coffee.persistence.dao.EmployeeDAO;
import org.coffee.persistence.entity.Employee;
import org.coffee.service.interfaces.AuthenticationService;
import org.coffee.util.PasswordUtil;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.Optional;

@Named
@ApplicationScoped
@EmployeeAuth
public class EmployeeAuthService implements AuthenticationService {

    @Inject
    private EmployeeDAO employeeDAO;

    @EmployeeAuth
    @Inject
    private AuthResultFactory factory;

    @Override
    @Logged
    public AuthResult authenticate(String username, String password)
            throws AuthenticationException {
        try {
            Optional<Employee> optEmployee = employeeDAO.findByUsername(username);

            if(optEmployee.isPresent()) {
                Employee employee = optEmployee.get();

                if (PasswordUtil.checkPassword(password, employee.getPassword())) {
                    return factory.create(true, employee);
                }
            }

            return factory.create(false, null);
        }
        catch (Exception e) {
            throw new AuthenticationException("An error occurred during user authentication.", e);
        }
    }
}
