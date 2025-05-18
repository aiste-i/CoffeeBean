package org.coffee.web;

import lombok.Getter;
import lombok.Setter;
import org.coffee.persistence.dao.IngredientDAO;
import org.coffee.persistence.dao.ProductDAO;
import org.coffee.persistence.entity.Ingredient;
import org.coffee.persistence.entity.IngredientType;
import org.coffee.persistence.entity.Product;

import javax.annotation.ManagedBean;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Named
@ViewScoped
@Getter
@Setter
public class ShowProductBean implements Serializable {

    @Inject
    private ProductDAO productDAO;

    @Inject
    private IngredientDAO ingredientDAO;

    private Product selectedProduct;

    private List<Product> productList;

    private int quantity = 1;

    private List<Ingredient> selectedAddons = new ArrayList<>();

    private Map<Long, List<Ingredient>> availableAddonsByType = new HashMap<>();

    public void setSelectedProduct(Product product) {
        if (product != null) {
            System.out.println("[DEBUG_LOG] Setting selected product: " + product.getName() + " (ID: " + product.getId() + ")");
            this.selectedProduct = productDAO.findByIdWithRelationships(product.getId());

            if (this.selectedProduct.getCategory() == null) {
                System.out.println("[DEBUG_LOG] Selected product category is null");
            } else {
                System.out.println("[DEBUG_LOG] Selected product category: " + this.selectedProduct.getCategory().getName());
                System.out.println("[DEBUG_LOG] Category addon types size: " + 
                    (this.selectedProduct.getCategory().getAddonIngredientTypes() != null ? 
                     this.selectedProduct.getCategory().getAddonIngredientTypes().size() : "null"));
            }

            loadAvailableAddons();
        } else {
            System.out.println("[DEBUG_LOG] Setting selected product to null");
            this.selectedProduct = null;
        }
        // Reset selected addons when a new product is selected
        this.selectedAddons.clear();
    }

    private void loadAvailableAddons() {
        if (selectedProduct == null) {
            System.out.println("[DEBUG_LOG] loadAvailableAddons: selectedProduct is null");
            return;
        }

        System.out.println("[DEBUG_LOG] loadAvailableAddons for product: " + selectedProduct.getName());

        // Clear previous addons
        availableAddonsByType.clear();

        // Get all valid addon types for this product
        List<IngredientType> validTypes = selectedProduct.getValidAddonIngredientTypes();
        if (validTypes == null) {
            System.out.println("[DEBUG_LOG] loadAvailableAddons: validTypes is null");
            return;
        } else if (validTypes.isEmpty()) {
            System.out.println("[DEBUG_LOG] loadAvailableAddons: validTypes is empty");
            return;
        }

        System.out.println("[DEBUG_LOG] loadAvailableAddons: found " + validTypes.size() + " valid addon types");
        for (IngredientType type : validTypes) {
            System.out.println("[DEBUG_LOG] Valid addon type: " + type.getName() + " (ID: " + type.getId() + ")");
        }

        // Get all ingredients
        List<Ingredient> allIngredients = ingredientDAO.findAll();
        System.out.println("[DEBUG_LOG] loadAvailableAddons: found " + allIngredients.size() + " total ingredients");

        // Group ingredients by type
        for (IngredientType type : validTypes) {
            List<Ingredient> typeIngredients = allIngredients.stream()
                    .filter(i -> i.getType() != null && i.getType().getId().equals(type.getId()) && !i.isDeleted())
                    .collect(Collectors.toList());

            System.out.println("[DEBUG_LOG] For type " + type.getName() + " found " + typeIngredients.size() + " ingredients");

            if (!typeIngredients.isEmpty()) {
                availableAddonsByType.put(type.getId(), typeIngredients);
                System.out.println("[DEBUG_LOG] Added " + typeIngredients.size() + " ingredients for type " + type.getName());
                for (Ingredient ingredient : typeIngredients) {
                    System.out.println("[DEBUG_LOG] - " + ingredient.getName() + " (ID: " + ingredient.getId() + ")");
                }
            } else {
                System.out.println("[DEBUG_LOG] No ingredients found for type " + type.getName());
            }
        }

        System.out.println("[DEBUG_LOG] availableAddonsByType has " + availableAddonsByType.size() + " entries");
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
        selectedAddons.clear();
    }

    public List<IngredientType> getAvailableAddonTypes() {
        if (selectedProduct == null) {
            System.out.println("[DEBUG_LOG] getAvailableAddonTypes: selectedProduct is null");
            return new ArrayList<>();
        }

        System.out.println("[DEBUG_LOG] getAvailableAddonTypes for product: " + selectedProduct.getName());

        List<IngredientType> types = selectedProduct.getValidAddonIngredientTypes();
        if (types == null) {
            System.out.println("[DEBUG_LOG] getAvailableAddonTypes: types is null");
            return new ArrayList<>();
        } else if (types.isEmpty()) {
            System.out.println("[DEBUG_LOG] getAvailableAddonTypes: types is empty");
            return types;
        }

        System.out.println("[DEBUG_LOG] getAvailableAddonTypes: returning " + types.size() + " types");
        for (IngredientType type : types) {
            System.out.println("[DEBUG_LOG] Returning addon type: " + type.getName() + " (ID: " + type.getId() + ")");
        }

        return types;
    }

    public List<Ingredient> getAvailableAddonsForType(Long typeId) {
        System.out.println("[DEBUG_LOG] getAvailableAddonsForType for typeId: " + typeId);

        List<Ingredient> ingredients = availableAddonsByType.getOrDefault(typeId, new ArrayList<>());
        System.out.println("[DEBUG_LOG] getAvailableAddonsForType: returning " + ingredients.size() + " ingredients for typeId " + typeId);

        for (Ingredient ingredient : ingredients) {
            System.out.println("[DEBUG_LOG] - " + ingredient.getName() + " (ID: " + ingredient.getId() + ")");
        }

        return ingredients;
    }

    public void toggleAddon(Ingredient addon) {
        System.out.println("[DEBUG_LOG] toggleAddon called for: " + addon.getName() + " (ID: " + addon.getId() + ")");

        if (selectedAddons.contains(addon)) {
            System.out.println("[DEBUG_LOG] Removing addon: " + addon.getName());
            selectedAddons.remove(addon);
        } else {
            System.out.println("[DEBUG_LOG] Adding addon: " + addon.getName());
            selectedAddons.add(addon);
        }

        System.out.println("[DEBUG_LOG] Selected addons count: " + selectedAddons.size());
    }

    public boolean isAddonSelected(Ingredient addon) {
        return selectedAddons.contains(addon);
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
