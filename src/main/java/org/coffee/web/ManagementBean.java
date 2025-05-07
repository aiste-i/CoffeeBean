package org.coffee.web;

import lombok.Getter;
import lombok.Setter;
import org.coffee.persistence.dao.ProductDAO;
import org.coffee.persistence.entity.Product;


import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
@Getter
@Setter
public class ManagementBean implements Serializable {

    @Inject
    private ProductDAO productDAO;

    private Product newProduct = new Product();
    private Product selectedProduct;

    private List<Product> productList;

    public List<Product> getProductList() {
        if (productList == null) {
            productList = productDAO.findAll();
        }
        return productList;
    }


    public void addProduct() {
        productDAO.persist(newProduct);
        refreshProductList();
        newProduct = new Product(); // reset form
    }

    public void updateProduct() {
        productDAO.update(selectedProduct);
        refreshProductList();
        selectedProduct = null;
    }

    public void deleteProduct(Product product) {
        productDAO.removeById(product.getId());
        refreshProductList();
        if (selectedProduct != null && selectedProduct.equals(product)) {
            selectedProduct = null;
        }
    }

    public void selectProduct(Product product) {
        selectedProduct = product;
    }

    public void refreshProductList() {
        productList = productDAO.findAll();
    }

}
