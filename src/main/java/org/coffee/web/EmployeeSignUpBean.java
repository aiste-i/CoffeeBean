package org.coffee.web;

import lombok.Getter;
import lombok.Setter;
import org.coffee.annotations.Logged;
import org.coffee.persistence.entity.Employee;
import org.coffee.persistence.entity.enums.UserRole;
import org.coffee.service.interfaces.BusinessService;
import org.coffee.service.interfaces.RegistrationService;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Named
@RequestScoped
public class EmployeeSignUpBean {

    @EJB
    private BusinessService businessService;

    @Inject
    private RegistrationService registrationService;

    @Getter
    @Setter
    private String plainPassword;

    @Getter
    @Setter
    private Employee employee = new Employee();

    @Transactional
    @Logged
    public String signUp() {
        FacesContext context = FacesContext.getCurrentInstance();

        try {
            employee.setBusiness(businessService.getActiveBusiness());

            registrationService.registerEmployee(employee, plainPassword);

            context.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO,
                            "Sign Up Successful.",
                            "Employee '" + employee.getUsername() + "' created."));

            employee = new Employee();
            plainPassword = null;

            return null;

        } catch (Exception e) {
            context.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Sign Up Failed",
                            e.getCause().getMessage()));
            return null;
        }
    }

    public List<UserRole> getAvailableRoles() {
        return Arrays.stream(UserRole.values())
                .filter(role -> role != UserRole.ADMIN && role != UserRole.CUSTOMER)
                .collect(Collectors.toList());
    }
}
