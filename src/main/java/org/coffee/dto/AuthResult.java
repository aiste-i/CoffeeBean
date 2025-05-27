package org.coffee.dto;

import lombok.Getter;

public abstract class AuthResult {

    @Getter
    protected final boolean success;

    @Getter
    protected final Object user;

    protected AuthResult(boolean success, Object user){
        this.success = success;
        this.user = user;
    }
}
