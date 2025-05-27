package org.coffee.service.interfaces;

import org.coffee.persistence.entity.Business;

public interface BusinessService {

    Business getActiveBusiness();

    Long getActiveBusinessId();
}
