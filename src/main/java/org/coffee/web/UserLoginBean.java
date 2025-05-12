package org.coffee.web;

import lombok.Getter;
import lombok.Setter;
import org.coffee.persistence.dao.UserDAO;
import org.coffee.persistence.entity.User;
import org.coffee.persistence.entity.enums.UserRole;
import org.coffee.util.PasswordUtil;

import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Named
@RequestScoped
public class UserLoginBean {

    @Inject
    private UserDAO userDAO; // Your DAO for Employee data

    @Getter
    @Setter
    private String email;

    @Getter
    @Setter
    private String password;


    public String login() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();

        try {
            User user = userDAO.findByUsername(email);

            if (user != null && PasswordUtil.checkPassword(password, user.getPassword())) { // Check hashed password!
                HttpSession session = request.getSession(); // Get or create session
                session.setAttribute("loggedInUserType", "User"); // Mark type
                session.setAttribute("loggedInUserId", user.getId()); // Store ID
                session.setAttribute("loggedInUserRole", UserRole.CUSTOMER); // Store role
                session.setAttribute("loggedInUserEmail", user.getEmail());

                return "/user/menu.xhtml";

            } else {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Login failed.", "Invalid username or password."));
                return null;
            }
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Login failed.", "Invalid username or password."));
            return null;
        }
    }

    public String logout() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate(); // Invalidate session
        }
        return "/index.xhtml?faces-redirect=true";
    }
}
