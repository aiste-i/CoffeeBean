package org.coffee.web;

import lombok.Getter;
import lombok.Setter;
import org.coffee.service.BusinessService;

import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpSession;

@Named("changeBusinessEmailBean")
@RequestScoped
public class ChangeBusinessEmailBean {

    @Inject
    private BusinessService businessService;

    @Getter @Setter
    private String currentPassword;

    @Getter @Setter
    private String newEmail;

    public String changeEmail() {
        FacesContext ctx = FacesContext.getCurrentInstance();

        // get admin ID from session
        HttpSession session = (HttpSession) ctx
                .getExternalContext().getSession(false);
        Long adminId = (Long) session.getAttribute("loggedInUserId");

        try {
            businessService.updateBusinessEmail(adminId, currentPassword, newEmail);

            // keep messages across redirect
            ctx.getExternalContext().getFlash().setKeepMessages(true);
            ctx.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO,
                            "Business email updated successfully", null));

            // redirect back to dashboard
            return "/admin/dashboard.xhtml?faces-redirect=true";

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
