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
public class ProductManagementBean implements Serializable {

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

    public void openNew() {
        selectedProduct = new Product();
    }

    public void saveProduct() {
        if (selectedProduct.getId() == null) {
            productDAO.persist(selectedProduct);
        } else {
            productDAO.update(selectedProduct);
        }
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

    public void refreshProductList() {
        productList = productDAO.findAll();
    }
}
