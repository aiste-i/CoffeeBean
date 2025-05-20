package org.coffee.web;

import lombok.Getter;
import lombok.Setter;
import org.coffee.persistence.entity.User;
import org.coffee.service.interfaces.RegistrationServiceInterface;

import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;
import java.io.Serializable;

@Named
@RequestScoped
public class UserSignUpBean implements Serializable {

    @Inject
    private RegistrationServiceInterface registrationService;

    @Getter
    @Setter
    private String email;

    @Getter
    @Setter
    private String plainPassword;

    @Getter
    @Setter
    private String plainConfirmPassword;

    @Inject
    private UserLoginBean userLoginBean;

    @Transactional
    public String signUp() {
        User user = new User();

        FacesContext context = FacesContext.getCurrentInstance();

        try {
            user.setEmail(email);
            registrationService.registerUser(user, plainPassword);

            userLoginBean.setEmail(email);
            userLoginBean.setPassword(plainPassword);

            return userLoginBean.login();

        }
        catch (Exception e) {
            context.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Sign up failed.",
                            e.getCause().getMessage()));
            return null;
        }
    }
}
