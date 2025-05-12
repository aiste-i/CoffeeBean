package org.coffee.web;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.coffee.persistence.dao.PasswordResetDAO;
import org.coffee.persistence.dao.UserDAO;
import org.coffee.persistence.entity.PasswordReset;
import org.coffee.persistence.entity.User;

import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

@Named
@SessionScoped
@NoArgsConstructor
public class PasswordForgotBean implements Serializable {

    @Inject
    private EmailSenderBean emailSenderBean;

    @Inject
    private TokenBean tokenBean;

    @Inject
    private UserDAO userDAO;

    @Inject
    private PasswordResetDAO resetDAO;

    @Getter
    @Setter
    private String email;

    @Getter
    private String token;

    @Getter
    @Setter
    private User user = new User();

    @Getter
    @Setter
    private PasswordReset reset = new PasswordReset();


    public void requestPasswordChange() {
        FacesContext context = FacesContext.getCurrentInstance();

        try{
            user = userDAO.findByUsername(email);

            if(user != null){
                token = tokenBean.generateToken();

                reset.setUser(user);
                reset.setToken(token);

                resetDAO.persist(reset);

                // change later to BaseUrlProvider.getBaseUrl() + "/user/reset-password.xhtml?token=" + token
                String resetUrl = "http://localhost:8080/coffee-1.0-SNAPSHOT/user/reset-password.xhtml?token=" + token;

                emailSenderBean.setDefaultSender(true);
                emailSenderBean.setRecipient(email);
                emailSenderBean.setSubject("CoffeeBean: Reset Your Password");
                emailSenderBean.setBody("Click to reset your password: " + resetUrl);

                emailSenderBean.sendEmail();

                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "E-mail sent.", "OK"));
                return;
            }

            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Password reset failed.", "Invalid e-mail."));
        }
        catch (Exception e){
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Password reset failed.", e.getMessage()));
        }
    }
}
