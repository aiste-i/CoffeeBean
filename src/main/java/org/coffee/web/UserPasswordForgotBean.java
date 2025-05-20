package org.coffee.web;

import lombok.Getter;
import lombok.Setter;
import org.coffee.service.interfaces.UserServiceInterface;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import java.io.Serializable;

@Named
@RequestScoped
public class UserPasswordForgotBean implements Serializable {

    @EJB
    private UserServiceInterface userService;

    @Getter
    @Setter
    private String email;


    public void requestPasswordReset() {
        FacesContext context = FacesContext.getCurrentInstance();

        try{
            // we get the result of the password reset request
            boolean result = userService.requestPasswordReset(email);

            if(result) {
                context.addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO,
                                "Success.",
                                "E-mail sent."));
                return;
            }

            // request returns false if user under the given email doesn't exist
            context.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Password reset request failed.",
                            "Invalid credentials."));
        }
        catch (Exception e){
            context.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Password reset request failed.",
                            e.getCause().getMessage()));
        }
    }
}
