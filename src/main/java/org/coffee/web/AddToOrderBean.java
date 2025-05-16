package org.coffee.web;

import lombok.Getter;
import lombok.Setter;
import org.coffee.persistence.dao.IngredientDAO;
import org.coffee.persistence.dao.OrderItemDAO;
import org.coffee.persistence.dao.ProductDAO;
import org.coffee.persistence.entity.Ingredient;
import org.coffee.persistence.entity.OrderItem;
import org.coffee.persistence.entity.Product;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Named
@ViewScoped
@Getter
@Setter
public class AddToOrderBean implements Serializable{
    @Inject
    private ProductDAO productDao;

    @Inject
    private IngredientDAO ingredientDao;

    @Inject
    private OrderItemDAO orderItemDao;

    private Product selectedProduct;

    private List<Ingredient> availableAddOns;

    private List<Ingredient> selectedAddOns = new ArrayList<>();

    private int quantity = 1;

    public void openAddToCartDialog(Product product) {
        this.selectedProduct = product;
        this.selectedAddOns = new ArrayList<>();
        this.quantity = 1;
        loadAvailableAddOns(product);
    }

    public void addToCart() {
        if (selectedProduct == null || quantity < 1) {
            System.out.println("Product is null or quantity is invalid.");
            return;
        }

        System.out.println("Selected product: " + selectedProduct.getName());
        System.out.println("Quantity: " + quantity);
        System.out.println("Selected add-ons: ");
        for (Ingredient ingredient : selectedAddOns) {
            System.out.println("- " + ingredient.getName());
        }

        OrderItem orderItem = new OrderItem();
        orderItem.setProduct(selectedProduct);
        orderItem.setAddons(selectedAddOns);
        orderItem.setQuantity(quantity);
        orderItem.setName(selectedProduct.getName());
        orderItem.setPriceAtOrder(orderItem.calculatePrice());

        orderItemDao.persist(orderItem);

        clearSelection();
    }

    private void loadAvailableAddOns(Product product) {
        if (product != null && product.getCategory() != null) {
            availableAddOns = ingredientDao.findAddOnsForCategory(product.getCategory().getId());
        } else {
            availableAddOns = new ArrayList<>();
        }
    }

    private void clearSelection() {
        selectedProduct = null;
        selectedAddOns = new ArrayList<>();
        quantity = 1;
        availableAddOns = null;
    }



}
