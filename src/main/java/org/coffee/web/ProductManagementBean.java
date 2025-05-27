package org.coffee.web;

import lombok.Getter;
import lombok.Setter;
import org.coffee.persistence.dao.ProductCategoryDAO;
import org.coffee.persistence.dao.ProductDAO;
import org.coffee.persistence.entity.Product;
import org.coffee.persistence.entity.ProductCategory;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Named
@ViewScoped
@Getter
@Setter
public class ProductManagementBean implements Serializable {

    @Inject
    private ProductDAO productDAO;

    @Inject
    private ProductCategoryDAO productCategoryDAO;

    private Product selectedProduct;
    private Long productCategoryId;
    private List<Product> productList;
    private Map<Long, ProductCategory> productCategoryDictionary;

    public void loadProductCategories() {
        if(productCategoryDictionary == null) {
            productCategoryDictionary = productCategoryDAO.findAll()
            .stream().collect(Collectors.toMap(ProductCategory::getId, Function.identity()));
        }
    }

    public List<Product> getProductList() {
        productList = productDAO.findAll();
        return productList;
    }

    public void openNew() {
        selectedProduct = new Product();
        loadProductCategories();
    }

    @Transactional
    public void saveProduct() {
        FacesContext context = FacesContext.getCurrentInstance();

        try {
            if (selectedProduct != null) {
                ProductCategory productCategory = productCategoryDictionary.get(productCategoryId);
                selectedProduct.setCategory(productCategory);

                if(selectedProduct.getId() == null) {
                    productDAO.persist(selectedProduct);
                    getProductList();
                }
                else {
                    productDAO.update(selectedProduct);
                }
            }
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Operation failed.", "Product already exists"));
        }

        getProductList();
        selectedProduct = null;
    }

    public void deleteProduct(Product product) {
        productDAO.removeById(product.getId());
        getProductList();
        if (selectedProduct != null && selectedProduct.equals(product)) {
            selectedProduct = null;
        }
    }

}
