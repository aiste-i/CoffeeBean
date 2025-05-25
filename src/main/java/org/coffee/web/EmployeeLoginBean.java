package org.coffee.web;

import lombok.Getter;
import lombok.Setter;
import org.coffee.persistence.dao.EmployeeDAO;
import org.coffee.persistence.entity.Employee;
import org.coffee.persistence.entity.enums.UserRole;
import org.coffee.service.EmployeeService;
import org.coffee.util.PasswordUtil;

import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Optional;

import static org.coffee.constants.Constants.SessionAttributeKeys.*;

@Named
@RequestScoped
public class EmployeeLoginBean {

    @Inject
    private EmployeeDAO employeeDAO;

    @Getter
    @Setter
    private String username;

    @Getter
    @Setter
    private String password;

    @Inject
    private EmployeeService employeeService;

    public String login() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();

        Optional<Employee> employeeOpt = employeeService.getEmployee(this.username);

        if (!employeeOpt.isPresent()) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Login Failed", "Invalid username/email or password."));
            return null;
        }

        Employee employee = employeeOpt.get();

        if (PasswordUtil.checkPassword(password, employee.getPassword())) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Login Failed", "Invalid username/email or password."));
            return null;
        }

        HttpSession oldSession = request.getSession(false);
        if (oldSession != null) {
            oldSession.invalidate();
        }
        HttpSession session = request.getSession(true);
        session.setAttribute(LOGGED_IN_USER_ID, employee.getId());
        session.setAttribute(LOGGED_IN_USER_ROLE, employee.getRole());
        session.setAttribute(LOGGED_IN_USERNAME, employee.getUsername());

        if(employee.getRole().equals(UserRole.EMPLOYEE))
            return "/admin/index.xhtml?faces-redirect=true";

        return "/admin/dashboard.xhtml?faces-redirect=true";
    }

    public String logout() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return "/index.xhtml?faces-redirect=true";
    }
}
