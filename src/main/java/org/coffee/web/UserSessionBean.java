package org.coffee.web;

import org.coffee.persistence.entity.User;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import java.io.Serializable;

@Named
@SessionScoped
public class UserSessionBean implements Serializable {
    private boolean loggedIn = false;
    private User loggedInUserEntity; // Populate this on successful login

    // Hardcode for testing, replace with actual login logic
    public void loginTestUser() {
        loggedInUserEntity = new User(); // Create a mock User
        loggedInUserEntity.setId(1L);
        loggedInUserEntity.setEmail("test@example.com");
        // Set other User fields if needed
        this.loggedIn = true;
    }
    public void logout() {
        this.loggedIn = false;
        this.loggedInUserEntity = null;
        // FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        // Navigate to login page
    }

    public boolean isLoggedIn() { return loggedIn; }
    public User getLoggedInUserEntity() { return loggedInUserEntity; }
    public Long getLoggedInUserId() { return loggedInUserEntity != null ? loggedInUserEntity.getId() : null; }
}