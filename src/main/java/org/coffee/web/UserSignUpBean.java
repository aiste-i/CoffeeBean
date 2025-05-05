package org.coffee.web;

import lombok.Getter;
import lombok.Setter;
import org.coffee.persistence.dao.UserDAO;
import org.coffee.persistence.entity.User;
import org.coffee.util.PasswordUtil;

import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

@Named
@RequestScoped
public class UserSignUpBean {

    @Inject
    private UserDAO userDAO;

    @Getter
    @Setter
    private String email;

    @Getter
    @Setter
    private String plainPassword;

    @Getter
    @Setter
    private String plainRepeatPassword;

    @Getter
    @Setter
    private User newUser = new User();

    @Transactional
    public String signUp() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest(); // Needed only for manual session/login

        try {
            newUser.setEmail(email);
            newUser.setPassword(PasswordUtil.hashPassword(plainPassword));

            userDAO.persist(newUser);

            User user = userDAO.findByUsername(email);
            if (user != null && PasswordUtil.checkPassword(plainPassword, user.getPassword())) {
                HttpSession session = request.getSession();
                session.setAttribute("loggedInUserType", "User"); // Mark type
                session.setAttribute("loggedInUserId", user.getId());
                session.setAttribute("loggedInUserEmail", user.getEmail());
            }

            return "/index.xhtml?faces-redirect=true";


        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Sign up failed.", "An unexpected error occurred."));
            return null; // Stay on the same page
        }
    }
}
