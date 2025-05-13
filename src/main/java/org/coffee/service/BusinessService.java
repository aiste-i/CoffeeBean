package org.coffee.service;

import org.coffee.persistence.dao.BusinessDAO;
import org.coffee.persistence.dao.UserDAO;
import org.coffee.persistence.entity.Business;
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

    public Long getActiveBusinessId() {
        return getActiveBusiness().getId();
    }
}
