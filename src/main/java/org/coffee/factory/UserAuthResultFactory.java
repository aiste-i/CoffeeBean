package org.coffee.factory;

import org.coffee.annotations.UserAuth;
import org.coffee.dto.UserAuthResult;
import org.coffee.persistence.entity.User;

import javax.enterprise.context.RequestScoped;

@UserAuth
@RequestScoped
public class UserAuthResultFactory implements AuthResultFactory{

    @Override
    public UserAuthResult create(boolean success, Object user)
            throws IllegalArgumentException {

        return new UserAuthResult(success, (User) user);
    }
}
