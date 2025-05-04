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
    private User newUser = new User();

    @Transactional
    public String signUp() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest(); // Needed only for manual session/login

        try {
            newUser.setEmail(email);
            newUser.setPassword(PasswordUtil.hashPassword(plainPassword));
            userDAO.persist(newUser);

            // Clear the form state (since it's RequestScoped, it's implicitly cleared, but good practice)
            newUser = new User();
            email = null;
            plainPassword = null;

            // Redirect to the admin login page after successful signup
            // Use faces-redirect=true for a clean GET request after POST
            return "/user/login.xhtml?faces-redirect=true";


        } catch (Exception e) {
            // Log the exception e properly using a Logger
            System.err.println("Error during sign up: " + e.getMessage()); // Replace with Logger
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Sign Up Failed", "An unexpected error occurred."));
            return null; // Stay on the same page
        }
    }
}
