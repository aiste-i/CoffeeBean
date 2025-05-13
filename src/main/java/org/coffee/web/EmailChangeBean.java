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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.Serializable;

@Named
@SessionScoped
@NoArgsConstructor
public class EmailChangeBean implements Serializable {

    @Inject
    private UserDAO userDAO;

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
            String email = session.getAttribute("loggedInUserEmail").toString();

            user = userDAO.findByUsername(email);

            user.setEmail(newEmail);
            userDAO.update(user);

            session.setAttribute("loggedInUserEmail", newEmail);

            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "E-mail address reset to:", newEmail));

            return null;
        }
        catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "E-mail change failed.", "Please enter a valid e-mail address."));
            return null; // Stay on the same page
        }
    }
}
