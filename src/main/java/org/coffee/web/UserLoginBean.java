package org.coffee.web;

import lombok.Getter;
import lombok.Setter;
import org.coffee.annotations.UserAuth;
import org.coffee.persistence.entity.User;
import org.coffee.persistence.entity.enums.UserRole;
import org.coffee.dto.UserAuthResult;
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
public class UserLoginBean implements Serializable {

    @Inject
    @UserAuth
    private AuthenticationService authService;

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
            UserAuthResult result = (UserAuthResult) authService.authenticate(email, password);

            if (result.isSuccess()) {
                User user = result.getUser();
                HttpSession session = request.getSession(true);
                session.setAttribute(LOGGED_IN_USER_ID, user.getId());
                session.setAttribute(LOGGED_IN_USER_ROLE, UserRole.CUSTOMER);
                session.setAttribute(LOGGED_IN_USER_EMAIL, user.getEmail());

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
