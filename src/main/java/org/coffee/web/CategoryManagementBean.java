package org.coffee.web;

import lombok.Getter;
import lombok.Setter;
import org.coffee.annotations.Logged;
import org.coffee.persistence.dao.IngredientTypeDAO;
import org.coffee.persistence.dao.ProductCategoryDAO;
import org.coffee.persistence.entity.IngredientType;
import org.coffee.persistence.entity.ProductCategory;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@Named
@ViewScoped
@Getter
@Setter
public class CategoryManagementBean implements Serializable {

    @Inject
    private ProductCategoryDAO categoryDAO;

    private ProductCategory selectedCategory;

    private List<ProductCategory> categoryList;

    @Inject
    private IngredientTypeDAO ingredientTypeDAO;

    private Set<Long> selectedIngredientTypeIds; // Changed from List to Set
    private List<IngredientType> allIngredientTypes;

    public List<IngredientType> getAllIngredientTypes() {
        if (allIngredientTypes == null) {
            allIngredientTypes = ingredientTypeDAO.findAll();
        }
        return allIngredientTypes;
    }

    public List<ProductCategory> getCategoryList() {
        if (categoryList == null) {
            categoryList = categoryDAO.findAll();
        }
        return categoryList;
    }

    public void openNew() {
        selectedCategory = new ProductCategory();
    }

    @Transactional
    @Logged
    public void saveCategory() {
        if (selectedIngredientTypeIds != null) {
            List<IngredientType> selectedTypes = ingredientTypeDAO.findByIds(selectedIngredientTypeIds);
            selectedCategory.setAddonIngredientTypes(new HashSet<>(selectedTypes)); // Wrap in Set
        }

        if (selectedCategory.getId() == null) {
            categoryDAO.persist(selectedCategory);
        } else {
            categoryDAO.update(selectedCategory);
        }

        refreshCategoryList();
        selectedCategory = null;
        selectedIngredientTypeIds = null;
    }

    @Transactional
    @Logged
    public void deleteCategory(ProductCategory category) {
        categoryDAO.removeById(category.getId());
        refreshCategoryList();
        if (selectedCategory != null && selectedCategory.equals(category)) {
            selectedCategory = null;
        }
    }

    public void refreshCategoryList() {
        categoryList = categoryDAO.findAll();
    }

    public void setSelectedCategory(ProductCategory category) {
        this.selectedCategory = categoryDAO.findByIdWithAddonIngredientTypes(category.getId());
        this.selectedIngredientTypeIds = category.getAddonIngredientTypes().stream()
                .map(IngredientType::getId)
                .collect(Collectors.toSet());
    }

}
