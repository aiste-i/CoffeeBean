package org.coffee.web;

import lombok.Getter;
import lombok.Setter;
import org.coffee.persistence.dao.ProductDAO;
import org.coffee.persistence.entity.IngredientType;
import org.coffee.persistence.entity.Product;

import javax.annotation.ManagedBean;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

@Named
@ViewScoped
@Getter
@Setter
public class ShowProductBean implements Serializable {

    @Inject
    private ProductDAO productDAO;

    private Product selectedProduct;

    private List<Product> productList;

    private int quantity = 1;

    public void setSelectedProduct(Product product) {
        if (product != null) {
            this.selectedProduct = productDAO.findByIdWithRelationships(product.getId());
        } else {
            this.selectedProduct = null;
        }
    }

    public List<Product> getProductList() {
        if (productList == null) {
            productList = productDAO.findAll();
        }
        return productList;
    }

    public void refreshProducts() {
        productList = productDAO.findAll();
    }

    public void resetQuantity() {
        quantity = 1;
    }

    public String getAvailableAddons() {
        if (selectedProduct == null) {
            System.out.println("[DEBUG_LOG] Selected product is null");
            return "";
        }

        System.out.println("[DEBUG_LOG] Selected product: " + selectedProduct.getName());

        if (selectedProduct.getCategory() == null) {
            System.out.println("[DEBUG_LOG] Product category is null");
        } else {
            System.out.println("[DEBUG_LOG] Product category: " + selectedProduct.getCategory().getName());
        }

        List<IngredientType> validTypes = selectedProduct.getValidAddonIngredientTypes();
        if (validTypes == null) {
            System.out.println("[DEBUG_LOG] Valid addon types is null");
            return "No add-ons available for this product.";
        } else if (validTypes.isEmpty()) {
            System.out.println("[DEBUG_LOG] Valid addon types is empty");
            return "No add-ons available for this product.";
        }

        System.out.println("[DEBUG_LOG] Valid addon types size: " + validTypes.size());
        validTypes.forEach(type -> System.out.println("[DEBUG_LOG] Addon type: " + type.getName()));

        String addonTypes = validTypes.stream()
                .map(IngredientType::getName)
                .collect(Collectors.joining(", "));

        return "Available add-ons: " + addonTypes;
    }
}
