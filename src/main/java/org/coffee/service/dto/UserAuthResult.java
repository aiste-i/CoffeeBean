package org.coffee.service.dto;

import lombok.Getter;
import org.coffee.persistence.entity.User;

public class UserAuthResult {

    @Getter
    private final boolean success;

    @Getter
    private final User user;

    private UserAuthResult(boolean success, User user){
        this.success = success;
        this.user = user;
    }

    public static UserAuthResult create(boolean success, User user) {
        return new UserAuthResult(success, user);
    }
}
