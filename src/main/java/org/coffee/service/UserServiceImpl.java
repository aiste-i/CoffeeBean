package org.coffee.service;

import org.coffee.annotations.Development;
import org.coffee.persistence.dao.PasswordResetDAO;
import org.coffee.persistence.dao.UserDAO;
import org.coffee.persistence.entity.PasswordReset;
import org.coffee.persistence.entity.User;
import org.coffee.exception.CredentialChangeException;
import org.coffee.exception.EmailException;
import org.coffee.service.interfaces.EmailService;
import org.coffee.service.interfaces.TokenService;
import org.coffee.service.interfaces.UserService;
import org.coffee.util.PasswordUtil;
import org.coffee.util.UrlProvider;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.Optional;

@Stateless
public class UserServiceImpl implements UserService {

    @Inject
    private UserDAO userDAO;

    @Inject
    private PasswordResetDAO resetDAO;

    @Inject
    private TokenService tokenService;

    @Inject
    private EmailService emailService;

    @Inject
    @Development
    private UrlProvider urlProvider;

    public User getUserById(Long id) {
        return userDAO.find(id);
    }

    @Transactional
    public boolean requestPasswordReset(String email) throws CredentialChangeException {
        try {
            Optional<User> optUser = userDAO.findUserByEmail(email);
            PasswordReset reset = new PasswordReset();

            if(optUser.isPresent()){
                User user = optUser.get();
                String token = tokenService.generateToken();

                reset.setUser(user);
                reset.setToken(token);

                resetDAO.persist(reset);

                // change later to BaseUrlProvider.getBaseUrl() + "/user/reset-password.xhtml?token=" + token
                String resetUrl = urlProvider.getBaseUrl() + "/user/reset-password.xhtml?token=" + token;
                String subject = "CoffeeBean: Reset Your Password";
                String body = "Click to reset your password: " + resetUrl;

                emailService.sendEmail(email, subject, body);

                return true;
            }

            return false;
        }
        catch (EmailException e) {
            throw new CredentialChangeException("Password reset email could not be sent.", e);
        }
        catch (Exception e) {
            throw new CredentialChangeException("An error occurred requesting password reset e-mail.", e);
        }
    }

    @Transactional
    public boolean resetPassword(String token, String password) throws CredentialChangeException {
        try {
            PasswordReset reset = resetDAO.findValidResetByToken(token);
            User user = reset.getUser();

            if(!PasswordUtil.checkPassword(password, user.getPassword())){
                user.setPassword(PasswordUtil.hashPassword(password));
                userDAO.update(user);

                reset.setRedeemed(true);
                resetDAO.update(reset);

                return true;
            }

            return false;
        }
        catch (IllegalArgumentException e) {
            throw new CredentialChangeException("Malformed password reset request.", e);
        }
        catch (Exception e) {
            throw new CredentialChangeException("An error occurred during password reset.", e);
        }
    }

    public boolean validatePasswordResetToken(String token) {
        if (token == null || token.isEmpty()) return false;

        PasswordReset reset = resetDAO.findValidResetByToken(token);
        return reset != null;
    }

    public boolean changeEmail(User user, String email) throws CredentialChangeException {
        try {
            if (user == null) {
                return false;
            }

            if(email.equals(user.getEmail())){
                throw new CredentialChangeException("New e-mail matches old one.");
            }

            user.setEmail(email);
            userDAO.update(user);

            return true;
        }
        catch (CredentialChangeException e){
            throw e;
        }
        catch (Exception e){
            throw new CredentialChangeException("An error occurred while changing e-mail.");
        }
    }

    @Transactional
    public void updatePassword(Long userId, String currentPassword, String newPassword) {
        User user = userDAO.find(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        if (!PasswordUtil.checkPassword(currentPassword, user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        user.setPassword(PasswordUtil.hashPassword(newPassword));
        userDAO.update(user);
    }
}
