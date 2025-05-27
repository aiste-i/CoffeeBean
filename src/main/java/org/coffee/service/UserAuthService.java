package org.coffee.service;

import org.coffee.annotations.Logged;
import org.coffee.annotations.UserAuth;
import org.coffee.dto.AuthResult;
import org.coffee.factory.AuthResultFactory;
import org.coffee.factory.UserAuthResultFactory;
import org.coffee.persistence.dao.UserDAO;
import org.coffee.persistence.entity.User;
import org.coffee.exception.AuthenticationException;
import org.coffee.service.interfaces.AuthenticationService;
import org.coffee.util.PasswordUtil;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.Optional;

@Named
@ApplicationScoped
@UserAuth
public class UserAuthService implements AuthenticationService {

    @Inject
    private UserDAO userDAO;

    @Inject
    @UserAuth
    private AuthResultFactory factory;

    @Override
    @Logged
    public AuthResult authenticate(String email, String password)
            throws AuthenticationException {
        try {
            Optional<User> optUser = userDAO.findUserByEmail(email);

            if(optUser.isPresent()) {
                User user = optUser.get();

                if (PasswordUtil.checkPassword(password, user.getPassword())) {
                    return factory.create(true, user);
                }
            }

            return factory.create(false, null);
        }
        catch (Exception e) {
            throw new AuthenticationException("An error occurred during user authentication.", e);
        }
    }
}
