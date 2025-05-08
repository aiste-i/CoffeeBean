package org.coffee.web;

import lombok.Getter;
import lombok.Setter;
import org.coffee.persistence.dao.ProductDAO;
import org.coffee.persistence.entity.Employee;
import org.coffee.persistence.entity.Product;
import org.coffee.util.PasswordUtil;

import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;

@Named
@RequestScoped
public class ShowPorductBean {
    @Inject
    private ProductDAO productDAO;
}
