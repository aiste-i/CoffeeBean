package org.coffee.web;

import lombok.Getter;
import lombok.Setter;
import org.coffee.annotations.EmployeeAuth;
import org.coffee.persistence.entity.Employee;
import org.coffee.dto.EmployeeAuthResult;
import org.coffee.exception.AuthenticationException;
import org.coffee.service.interfaces.AuthenticationService;

import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.Serializable;

import static org.coffee.constants.Constants.SessionAttributeKeys.*;

@Named
@RequestScoped
public class EmployeeLoginBean implements Serializable {

    @Inject
    @EmployeeAuth
    private AuthenticationService authService;

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
            EmployeeAuthResult result = (EmployeeAuthResult) authService.authenticate(username, password);

            System.out.println(result.getUser().getRole() + " " + result.isSuccess());
            if (result.isSuccess()) {
                Employee employee = result.getUser();
                HttpSession session = request.getSession(true);
                session.setAttribute(LOGGED_IN_USER_ID, employee.getId());
                session.setAttribute(LOGGED_IN_USER_ROLE, employee.getRole());
                session.setAttribute(LOGGED_IN_USERNAME, employee.getEmail());

                return "/admin/dashboard.xhtml?faces-redirect=true";
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
