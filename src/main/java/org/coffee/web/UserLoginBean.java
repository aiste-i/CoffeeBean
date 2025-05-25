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

import static org.coffee.constants.Constants.SessionAttributeKeys.*;

@Named
@RequestScoped
public class UserLoginBean {

    @Inject
    private UserDAO userDAO;

    @Inject
    private UserSessionBean userSession;

    @Getter
    @Setter
    private String email;

    @Getter
    @Setter
    private String password;
    @Named
    @Inject
    private UserSessionBean userSessionBean;


    public String login() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();

        try {
            User user = userDAO.findByUsername(email);

            if (user != null && PasswordUtil.checkPassword(password, user.getPassword())) {
                HttpSession oldSession = request.getSession(false);
                if (oldSession != null) {
                    oldSession.invalidate();
                }
                HttpSession session = request.getSession();
                session.setAttribute(LOGGED_IN_USER_ID, user.getId());
                session.setAttribute(LOGGED_IN_USER_ROLE, UserRole.CUSTOMER);
                session.setAttribute(LOGGED_IN_USER_EMAIL, user.getEmail());

                userSessionBean.establishSession(user);
                return "/user/menu.xhtml?faces-redirect=true";

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
            session.invalidate();
            userSessionBean.clearSessionData();
        }
        return "/index.xhtml?faces-redirect=true";
    }
}
