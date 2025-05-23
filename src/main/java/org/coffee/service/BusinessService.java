package org.coffee.service;

import org.coffee.persistence.dao.BusinessDAO;
import org.coffee.persistence.dao.EmployeeDAO;
import org.coffee.persistence.dao.UserDAO;
import org.coffee.persistence.entity.Business;
import org.coffee.persistence.entity.Employee;
import org.coffee.persistence.entity.User;
import org.coffee.util.PasswordUtil;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class BusinessService {

    @Inject
    private BusinessDAO businessDAO;

    @Inject
    private UserDAO userDAO;

    @Inject
    private EmployeeDAO employeeDAO;

    /**
     * Returns the single active Business in the system.
     */
    public Business getActiveBusiness() {
        List<Business> businesses = businessDAO.findAll();

        if (businesses.size() == 1) {
            return businesses.get(0);
        } else if (businesses.isEmpty()) {
            throw new IllegalStateException("Configuration error: No active Business found in the database.");
        } else {
            throw new IllegalStateException("Configuration error: More than one Business found. Single-business mode requires exactly one.");
        }
    }

    /**
     * Helper to fetch the ID of the active Business.
     */
    public Long getActiveBusinessId() {
        return getActiveBusiness().getId();
    }

    /**
     * Change a user's password.
     */
    @Transactional
    public void updatePassword(Long userId, String currentPassword, String newPassword) {
        User user = userDAO.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        if (!PasswordUtil.checkPassword(currentPassword, user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        user.setPassword(PasswordUtil.hashPassword(newPassword));
        userDAO.merge(user);
    }

    /**
     * Change the business email—admin must re‐enter their password for confirmation.
     *
     * @param adminId         the database ID of the logged‐in employee
     * @param currentPassword the admin's plain-text password to verify
     * @param newEmail        the new business email address
     */
    @Transactional
    public void updateBusinessEmail(Long adminId, String currentPassword, String newEmail) {
        // 1) verify admin credentials
        Employee admin = employeeDAO.findById(adminId);
        if (admin == null) {
            throw new IllegalArgumentException("Admin not found");
        }
        if (!PasswordUtil.checkPassword(currentPassword, admin.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        // 2) load the single Business
        Business business = getActiveBusiness();

        // 3) update its email and save
        business.setEmail(newEmail);
        businessDAO.update(business);
    }
}
