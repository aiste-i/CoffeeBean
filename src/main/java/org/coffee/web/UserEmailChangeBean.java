package org.coffee.web;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.coffee.persistence.entity.User;
import org.coffee.service.interfaces.UserServiceInterface;

import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.Serializable;

@Named
@SessionScoped
@NoArgsConstructor
public class UserEmailChangeBean implements Serializable {

    @Inject
    private UserSessionBean userSessionBean;

    @EJB
    private UserServiceInterface userService;

    @Getter
    @Setter
    private String newEmail;

    @Getter
    @Setter
    private User user = new User();

    public String submit() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        HttpSession session = request.getSession(false);

        try {
            User user = (User) userSessionBean.getLoggedInUser();

            boolean result = userService.changeEmail(user, newEmail);

            if (result) {
                session.setAttribute("loggedInUserEmail", newEmail);
                context.addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO,
                                "E-mail address reset to:",
                                newEmail));

                return "/user/menu.xhtml?faces-redirect=true";
            }

            context.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "E-mail change failed.",
                            "Invalid credentials."));

            return null;
        }
        catch (Exception e) {
            context.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "E-mail change failed.",
                            e.getCause().getMessage()));
            return null; // Stay on the same page
        }
    }
}
