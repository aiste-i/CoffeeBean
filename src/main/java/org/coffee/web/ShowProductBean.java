package org.coffee.web;

import lombok.Getter;
import lombok.Setter;
import org.coffee.persistence.dao.ProductDAO;
import org.coffee.persistence.entity.Product;

import javax.annotation.ManagedBean;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
@Getter
@Setter
public class ShowProductBean implements Serializable {

    @Inject
    private ProductDAO productDAO;

    private Product selectedProduct;

    private List<Product> productList;

    public List<Product> getProductList() {
        if (productList == null) {
            productList = productDAO.findAll();
        }
        return productList;
    }

    public void refreshProducts() {
        productList = productDAO.findAll();
    }

}
