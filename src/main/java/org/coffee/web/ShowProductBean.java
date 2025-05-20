package org.coffee.web;

import lombok.Getter;
import lombok.Setter;
import org.coffee.persistence.dao.IngredientDAO;
import org.coffee.persistence.dao.ProductDAO;
import org.coffee.persistence.entity.Ingredient;
import org.coffee.persistence.entity.IngredientType;
import org.coffee.persistence.entity.Product;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

    private Map<Long, String> selectedAddonsByType = new HashMap<>();

    private BigDecimal unitPrice;

    public void setSelectedProduct(Product product) {
        if (product != null) {
            this.selectedProduct = productDAO.findByIdWithRelationships(product.getId());
            loadAvailableAddons();
        } else {
            this.selectedProduct = null;
        }
        this.selectedAddons.clear();
        this.selectedAddonsByType.clear();
        getUnitPrice();
    }

    private void loadAvailableAddons() {
        if (selectedProduct == null) {
            return;
        }

        availableAddonsByType.clear();

        List<IngredientType> validTypes = selectedProduct.getValidAddonIngredientTypes();
        if (validTypes == null) {
            return;
        } else if (validTypes.isEmpty()) {
            return;
        }

        List<Ingredient> allIngredients = ingredientDAO.findAll();

        for (IngredientType type : validTypes) {
            List<Ingredient> typeIngredients = allIngredients.stream()
                    .filter(i -> i.getType() != null && i.getType().getId().equals(type.getId()) && !i.isDeleted())
                    .collect(Collectors.toList());

            if (!typeIngredients.isEmpty()) {
                availableAddonsByType.put(type.getId(), typeIngredients);
                }
        }
    }

    public List<Product> getProductList() {
        if (productList == null) {
            productList = productDAO.findAll();
        }
        return productList;
    }

    public void resetQuantity() {
        quantity = 1;
        selectedAddons.clear();
        selectedAddonsByType.clear();

        getUnitPrice();
    }

    public List<IngredientType> getAvailableAddonTypes() {
        if (selectedProduct == null) {
            return new ArrayList<>();
        }

        List<IngredientType> types = selectedProduct.getValidAddonIngredientTypes();
        if (types == null) {
            return new ArrayList<>();
        } else if (types.isEmpty()) {
            return types;
        }
        return types;
    }

    public List<Ingredient> getAvailableAddonsForType(Long typeId) {

        return availableAddonsByType.getOrDefault(typeId, new ArrayList<>());
    }

    public void selectAddonForType(Long typeId) {

        String selectedAddonId = selectedAddonsByType.get(typeId);
        if (selectedAddonId == null) {
            return;
        }
        updateSelectedAddonsList();
        getUnitPrice();
    }

    public List<Ingredient> updateSelectedAddonsList() {
        selectedAddons.clear();

        for (Map.Entry<Long, String> entry : selectedAddonsByType.entrySet()) {
            Long typeId = entry.getKey();
            String addonIdStr = entry.getValue();

            try {
                Long addonId = Long.parseLong(addonIdStr);

                List<Ingredient> addonsForType = availableAddonsByType.get(typeId);
                if (addonsForType != null) {
                    Optional<Ingredient> selectedAddon = addonsForType.stream()
                        .filter(addon -> addon.getId() == addonId)
                        .findFirst();

                    selectedAddon.ifPresent(addon -> {
                        selectedAddons.add(addon);
                    });
                }
            }catch (NumberFormatException e) {
            }
        }
        getUnitPrice();

        return selectedAddons;
    }

    public BigDecimal calculateAddonPrice() {
        if (selectedAddons == null || selectedAddons.isEmpty()) {
            return BigDecimal.ZERO;
        }

        return selectedAddons.stream()
                .map(Ingredient::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal calculateTotalPrice() {
        if (selectedProduct == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal basePrice = selectedProduct.getPrice() != null ? selectedProduct.getPrice() : BigDecimal.ZERO;
        BigDecimal addonPrice = calculateAddonPrice();

        return basePrice.add(addonPrice).multiply(new BigDecimal(quantity));
    }

    public BigDecimal getUnitPrice() {
        if (selectedProduct == null) {
            this.unitPrice = BigDecimal.ZERO;
            return this.unitPrice;
        }

        BigDecimal basePrice = selectedProduct.getPrice() != null ? selectedProduct.getPrice() : BigDecimal.ZERO;
        this.unitPrice = basePrice.add(calculateAddonPrice());
        return this.unitPrice;
    }
}
