package org.coffee.web;


import org.coffee.persistence.entity.enums.UserRole;
import org.coffee.service.interfaces.EmployeeServiceInterface;
import org.coffee.service.interfaces.UserServiceInterface;

import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.util.Objects;

@Named
@SessionScoped
public class UserSessionBean implements Serializable {

    @EJB
    private UserServiceInterface userService;

    @EJB
    private EmployeeServiceInterface employeeService;

    public String logout() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return "/index.xhtml?faces-redirect=true";
    }

    public Object getLoggedInUser() {
        Long id = getLoggedInUserId();
        UserRole role = getLoggedInUserRole();

        if (Objects.requireNonNull(role) == UserRole.CUSTOMER) {
            return userService.getUserById(id);
        }

        return employeeService.getEmployeeById(id);
    }

    public Long getLoggedInUserId() {
        FacesContext context = FacesContext.getCurrentInstance();
        if (context == null) {
            return null;
        }

        return (Long) context.getExternalContext()
                .getSessionMap()
                .get("loggedInUserId");
    }

    public String getLoggedInUserEmail() {
        FacesContext context = FacesContext.getCurrentInstance();
        if (context == null) {
            return null;
        }

        return (String) context.getExternalContext()
                .getSessionMap()
                .get("loggedInUserEmail");
    }

    public UserRole getLoggedInUserRole() {
        FacesContext context = FacesContext.getCurrentInstance();
        if (context == null) {
            return null;
        }

        return (UserRole) context.getExternalContext()
                .getSessionMap()
                .get("loggedInUserRole");
    }

    public boolean isLoggedIn() {
        return getLoggedInUserId() != null;
    }
}
