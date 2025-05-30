package org.coffee.service;

import org.coffee.annotations.Logged;
import org.coffee.persistence.dao.EmployeeDAO;
import org.coffee.persistence.entity.Employee;
import org.coffee.exception.CredentialChangeException;
import org.coffee.service.interfaces.EmployeeService;
import org.coffee.util.PasswordUtil;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.OptimisticLockException;
import javax.transaction.Transactional;
import java.util.Optional;

@Stateless
public class EmployeeServiceImpl implements EmployeeService {

    @Inject
    private EmployeeDAO employeeDAO;

    public Employee getEmployeeById(Long id) {
        return employeeDAO.find(id);
    }

    public Optional<Employee> getEmployee(String usernameOrEmailInput) {
        return employeeDAO.findByUsername(usernameOrEmailInput)
                .or(() -> employeeDAO.findByEmail(usernameOrEmailInput));
    }

    @Transactional
    @Logged
    public boolean changePassword(Employee employee, String oldPassword, String password)
            throws CredentialChangeException, OptimisticLockException {

        if(employee == null){
            throw new CredentialChangeException("An error occurred during e-mail change.");
        }

        if(!PasswordUtil.checkPassword(oldPassword, employee.getPassword())) {
            throw new CredentialChangeException("Invalid current password.");
        }

        employee.setPassword(PasswordUtil.hashPassword(password));
        employeeDAO.update(employee);

        return true;
    }
}
