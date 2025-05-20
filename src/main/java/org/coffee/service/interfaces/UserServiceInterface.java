package org.coffee.service.interfaces;

import org.coffee.persistence.entity.User;
import org.coffee.service.exceptions.CredentialChangeException;

public interface UserServiceInterface {

    User getUserById(Long id);

    boolean requestPasswordReset(String email) throws CredentialChangeException;

    boolean resetPassword(String token, String password) throws CredentialChangeException;

    boolean validatePasswordResetToken(String token);

    boolean changeEmail(User user, String email) throws CredentialChangeException;
}
