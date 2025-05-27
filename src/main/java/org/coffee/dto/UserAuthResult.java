package org.coffee.dto;

import org.coffee.persistence.entity.User;

public class UserAuthResult extends AuthResult {

    public UserAuthResult(boolean success, User user){
        super(success, user);
    }

    @Override
    public User getUser() {
        return (User) super.getUser();
    }
}
