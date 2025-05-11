package org.coffee.service;

import org.coffee.persistence.dao.EmployeeDAO;
import org.coffee.persistence.entity.Employee;
import org.coffee.util.PasswordUtil;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.OptimisticLockException;
import javax.security.auth.login.CredentialException;
import javax.transaction.Transactional;
import java.util.Optional;

@Stateless
public class EmployeeService {

    @Inject
    private EmployeeDAO employeeDAO;

    public Optional<Employee> getEmployee(String usernameOrEmailInput) {
        return employeeDAO.findByUsername(usernameOrEmailInput)
                .map(Optional::of)
                .orElseGet(() -> employeeDAO.findByEmail(usernameOrEmailInput));
    }

    @Transactional
    public void changePassword(String oldPassword, String newPassword, String username)
            throws CredentialException, OptimisticLockException {
        Optional<Employee> employeeOpt = getEmployee(username);

        if(!employeeOpt.isPresent())
            throw new CredentialException("Cannot change password");

        Employee employee = employeeOpt.get();
        if(!PasswordUtil.checkPassword(oldPassword, employee.getPassword()))
            throw new CredentialException("Invalid password");

        employee.setPassword(PasswordUtil.hashPassword(newPassword));

        employeeDAO.update(employee);
    }
}
