package org.coffee.dto;

import lombok.Getter;

@Getter
public abstract class AuthResult {

    protected final boolean success;

    protected final Object user;

    protected AuthResult(boolean success, Object user){
        this.success = success;
        this.user = user;
    }
}