package org.coffee.service;

import org.coffee.persistence.dao.UserDAO;
import org.coffee.persistence.entity.User;
import org.coffee.util.PasswordUtil;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

/**
 * Business logic specific to User entities.
 */
@ApplicationScoped
public class UserService {

    @Inject
    private UserDAO userDAO;

    /**
     * Change a user's password.
     *
     * @param userId          the database ID of the user
     * @param currentPassword the existing plain-text password to verify
     * @param newPassword     the new plain-text password
     * @throws IllegalArgumentException if the user is not found or current password is wrong
     */
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
