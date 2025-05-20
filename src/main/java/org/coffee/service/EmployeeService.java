package org.coffee.service;

import org.coffee.persistence.dao.EmployeeDAO;
import org.coffee.persistence.entity.Employee;
import org.coffee.service.exceptions.CredentialChangeException;
import org.coffee.service.interfaces.EmployeeServiceInterface;
import org.coffee.util.PasswordUtil;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.OptimisticLockException;
import javax.transaction.Transactional;
import java.util.Optional;

@Stateless
public class EmployeeService implements EmployeeServiceInterface {

    @Inject
    private EmployeeDAO employeeDAO;

    public Employee getEmployeeById(Long id) {
        return employeeDAO.find(id);
    }

    public Optional<Employee> getEmployee(String usernameOrEmailInput) {
        return employeeDAO.findByUsername(usernameOrEmailInput)
                .map(Optional::of)
                .orElseGet(() -> employeeDAO.findByEmail(usernameOrEmailInput));
    }

    @Transactional
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
