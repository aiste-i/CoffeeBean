package org.coffee.web;

import lombok.Getter;
import lombok.Setter;
import org.coffee.persistence.dao.PasswordResetDAO;
import org.coffee.persistence.dao.UserDAO;
import org.coffee.service.interfaces.UserServiceInterface;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

@Named
@RequestScoped
public class UserPasswordResetBean implements Serializable {

    @EJB
    private UserServiceInterface userService;

    @Inject
    private UserDAO userDAO;

    @Inject
    private PasswordResetDAO resetDAO;

    @Getter
    @Setter
    private String password;

    @Getter
    @Setter
    private String confirmPassword;

    @Getter
    @Setter
    private String token;

    @Getter
    private boolean tokenValid;


    public void init() {
        tokenValid = userService.validatePasswordResetToken(token);
    }

    public String submit() {
        FacesContext context = FacesContext.getCurrentInstance();

        try {
            // we check if password reset is successful
            boolean result = userService.resetPassword(token, password);

            if(result){
                return "/user/login.xhtml?faces-redirect=true";
            }

            // resetPassword() returns false when old and new passwords match
            context.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Password reset failed.",
                            "Current password matches new password."));
            return null;
        }
        catch (Exception e) {
            context.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Password reset failed.",
                            e.getCause().getMessage()));
            return null; // Stay on the same page
        }
    }
}
