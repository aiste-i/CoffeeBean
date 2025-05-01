package org.coffee.service;

import org.coffee.persistence.dao.BusinessDAO;
import org.coffee.persistence.entity.Business;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@ApplicationScoped // Or @Stateless if using EJB
public class BusinessService {

    @Inject
    private BusinessDAO businessDAO;

    public Business getActiveBusiness() {
        // Consider caching this result for performance if frequently needed within a request
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

//    // Optional: Add method for admin panel later to update business details
//    @Transactional
//    public Business updateBusinessDetails(Long id,) {
//        // Fetch, update, merge logic
//        // ...
//        return null; // return updated business
//    }
}
