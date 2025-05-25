package org.coffee.web;

import org.coffee.persistence.entity.User;
import org.coffee.persistence.entity.enums.UserRole;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import java.io.Serializable;

@Named
@SessionScoped
public class UserSessionBean implements Serializable {

    private boolean loggedIn = false;
    private User loggedInUserEntity;

    public void establishSession(User user) {
        if (user == null) {
            this.loggedInUserEntity = null;
            this.loggedIn = false;
            return;
        }
        this.loggedInUserEntity = user;
        this.loggedIn = true;
    }


    public void clearSessionData() {
        this.loggedIn = false;
        this.loggedInUserEntity = null;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public User getLoggedInUserEntity() {
        return loggedInUserEntity;
    }

    public Long getLoggedInUserId() {
        return loggedInUserEntity != null ? loggedInUserEntity.getId() : null;
    }

    public String getLoggedInUserEmail() {
        return loggedInUserEntity != null ? loggedInUserEntity.getEmail() : null;
    }

    public String getLoggedInUserRole() {
        return loggedInUserEntity != null ? UserRole.CUSTOMER.name() : null;
    }
}