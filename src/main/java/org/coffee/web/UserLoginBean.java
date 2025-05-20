package org.coffee.web;

import lombok.Getter;
import lombok.Setter;
import org.coffee.persistence.entity.User;
import org.coffee.persistence.entity.enums.UserRole;
import org.coffee.service.dto.UserAuthResult;
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
public class UserLoginBean implements Serializable {

    @Inject
    private AuthenticationServiceInterface authService;

    @Getter
    @Setter
    private String email;

    @Getter
    @Setter
    private String password;


    public String login() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();

        try {
            UserAuthResult result = authService.authenticateUser(email, password);

            if (result.isSuccess()) {
                User user = result.getUser();
                HttpSession session = request.getSession(true);
                session.setAttribute("loggedInUserId", user.getId());
                session.setAttribute("loggedInUserRole", UserRole.CUSTOMER);
                session.setAttribute("loggedInUserEmail", user.getEmail());

                return "/user/menu.xhtml?faces-redirect=true";
            }
            else {
                context.addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                "Login failed.",
                                "Invalid credentials."));
                return null;
            }
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
