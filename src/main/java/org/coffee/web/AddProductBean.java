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
public class AddProductBean {
    @Inject
    private ProductDAO productDAO;

    @Getter
    @Setter
    private Product newProduct = new Product();

    @Transactional
    public String addProduct() {
        FacesContext context = FacesContext.getCurrentInstance();

        try {
            productDAO.persist(newProduct);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Product Creation Successful",
                    "Product '" + newProduct.getName() + "' created."));

            newProduct = new Product();

            return null;

        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Product Creation Failed", "An unexpected error occurred."));
            return null;
        }

    }

}
