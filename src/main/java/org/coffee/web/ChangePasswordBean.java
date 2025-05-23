package org.coffee.web;

import lombok.Getter;
import lombok.Setter;
import org.coffee.service.UserService;

import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpSession;

@Named("changePasswordBean")
@RequestScoped
public class ChangePasswordBean {

    @Inject
    private UserService userService;        // ← inject UserService

    @Getter @Setter
    private String currentPassword;

    @Getter @Setter
    private String newPassword;

    @Getter @Setter
    private String confirmPassword;

    public String changePassword() {
        FacesContext ctx = FacesContext.getCurrentInstance();

        if (!newPassword.equals(confirmPassword)) {
            ctx.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Passwords do not match", null));
            return null;
        }

        HttpSession session = (HttpSession) ctx
                .getExternalContext().getSession(false);
        Long userId = (Long) session.getAttribute("loggedInUserId");

        try {
            userService.updatePassword(userId, currentPassword, newPassword);

            ctx.getExternalContext().getFlash().setKeepMessages(true);
            ctx.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO,
                            "Password changed successfully", null));

            return "/index.xhtml?faces-redirect=true";

        } catch (IllegalArgumentException ex) {
            ctx.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            ex.getMessage(), null));
            return null;
        } catch (Exception ex) {
            ctx.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Unexpected error", null));
            return null;
        }
    }
}
