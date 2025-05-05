package org.coffee.web;

import lombok.Getter;
import lombok.Setter;
import org.coffee.persistence.dao.BusinessDAO;
import org.coffee.persistence.entity.Business;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;
import java.io.Serializable;

@RequestScoped
@Named("businessBean")
public class BusinessBean implements Serializable {
    @Inject
    private BusinessDAO businessDAO;

    @Getter
    @Setter
    private Business business = new Business();

    @Transactional
    public void create(){
        businessDAO.persist(business);
    }
}
