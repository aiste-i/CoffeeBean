package org.coffee.web;

import org.coffee.persistence.entity.Business;
import org.coffee.service.BusinessService;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

@Named("businessBean")
@RequestScoped
public class BusinessBean {

    @Inject
    private BusinessService businessService;

    /** Returns the single configured Business */
    public Business getActiveBusiness() {
        return businessService.getActiveBusiness();
    }

    /** Shortcut for the email */
    public String getEmail() {
        return getActiveBusiness().getEmail();
    }
}
