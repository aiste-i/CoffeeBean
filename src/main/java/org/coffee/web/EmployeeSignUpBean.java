package org.coffee.web;

import lombok.Getter;
import lombok.Setter;
import org.coffee.persistence.dao.EmployeeDAO;
import org.coffee.persistence.entity.Employee;
import org.coffee.persistence.entity.enums.EmployeeRole;
import org.coffee.service.BusinessService;
import org.coffee.util.PasswordUtil;

import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Named
@RequestScoped
public class EmployeeSignUpBean {

    @Inject
    private BusinessService businessService;

    @Inject
    private EmployeeDAO employeeDAO;

    @Getter
    @Setter
    private String plainPassword;

    @Getter
    @Setter
    private Employee newEmployee = new Employee();

    @Transactional
    public String signUp() {
        FacesContext context = FacesContext.getCurrentInstance();

        try {
            newEmployee.setPassword(PasswordUtil.hashPassword(plainPassword));
            newEmployee.setBusiness(businessService.getActiveBusiness());
            employeeDAO.persist(newEmployee);

            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Sign Up Successful",
                    "Employee '" + newEmployee.getUsername() + "' created."));

            newEmployee = new Employee();
            plainPassword = null;

            return null;

        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Sign Up Failed", "An unexpected error occurred."));
            return null;
        }
    }

    public List<EmployeeRole> getAvailableRoles() {
        return Arrays.stream(EmployeeRole.values())
                .filter(role -> role != EmployeeRole.ADMIN) 
                .collect(Collectors.toList());
    }
}
