package org.coffee.web;

import lombok.Getter;
import lombok.Setter;
import org.coffee.persistence.entity.Employee;
import org.coffee.dto.EmployeeAuthResult;
import org.coffee.exception.AuthenticationException;
import org.coffee.service.interfaces.AuthenticationServiceInterface;

import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.Serializable;

@Named
@RequestScoped
public class EmployeeLoginBean implements Serializable {

    @Inject
    private AuthenticationServiceInterface authService;

    @Getter
    @Setter
    private String username;

    @Getter
    @Setter
    private String password;

    public String login() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();

        try {
            EmployeeAuthResult result = authService.authenticateEmployee(username, password);

            if (result.isSuccess()) {
                Employee employee = result.getEmployee();
                HttpSession session = request.getSession(true);
                session.setAttribute("loggedInUserId", employee.getId());
                session.setAttribute("loggedInUserRole", employee.getRole());
                session.setAttribute("loggedInUserEmail", employee.getEmail());

                return "/admin/employee-dashboard.xhtml?faces-redirect=true";
            }
            else {
                context.addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                "Login failed.",
                                "Invalid credentials."));
                return null;
            }
        }
        catch (AuthenticationException e) {
            context.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Login failed.",
                            "Authentication error occurred."));
            return null;
        }
        catch (Exception e) {
            context.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Login failed.",
                            e.getCause().getMessage()));
            return null;
        }
    }
}
