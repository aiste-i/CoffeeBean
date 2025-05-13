package org.coffee.web;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.coffee.persistence.dao.PasswordResetDAO;
import org.coffee.persistence.dao.UserDAO;
import org.coffee.persistence.entity.PasswordReset;
import org.coffee.persistence.entity.User;
import org.coffee.util.PasswordUtil;

import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

@Named
@SessionScoped
@NoArgsConstructor
public class PasswordResetBean implements Serializable {

    @Inject
    private UserDAO userDAO;

    @Inject
    private PasswordResetDAO resetDAO;

    @Getter
    @Setter
    private String newPassword;

    @Getter
    @Setter
    private String confirmPassword;

    @Getter
    @Setter
    private String token;

    @Getter
    private boolean tokenValid;

    @Getter
    @Setter
    private User user = new User();

    @Getter
    @Setter
    private PasswordReset reset = new PasswordReset();


    public void init() {
        tokenValid = validateToken(token);

        if(tokenValid){
            user = userDAO.find(reset.getUser().getId());
        }
    }

    private boolean validateToken(String token) {
        if (token == null || token.isEmpty()) return false;

        reset = resetDAO.findValidResetByToken(token);
        return reset != null;
    }

    public String submit() {
        FacesContext context = FacesContext.getCurrentInstance();

        try {
            if(!PasswordUtil.checkPassword(newPassword, user.getPassword())){
                user.setPassword(PasswordUtil.hashPassword(newPassword));
                userDAO.update(user);

                reset.setRedeemed(true);
                resetDAO.update(reset);

                FacesContext.getCurrentInstance().getExternalContext().invalidateSession();

                return "/user/login.xhtml?faces-redirect=true";
            }

            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Password reset failed.", "Current password matches new password."));

            return null;

        }
        catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Password reset failed.", "An unexpected error occurred."));
            return null; // Stay on the same page
        }
    }
}
