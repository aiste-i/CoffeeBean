package org.coffee.web;

import lombok.Getter;
import lombok.Setter;
import org.coffee.persistence.dao.IngredientDAO;
import org.coffee.persistence.dao.IngredientTypeDAO;
import org.coffee.persistence.dao.ProductCategoryDAO;
import org.coffee.persistence.entity.Ingredient;
import org.coffee.persistence.entity.IngredientType;
import org.coffee.persistence.entity.ProductCategory;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
@Getter
@Setter
public class IngTypeManagementBean implements Serializable {

    @Inject
    private IngredientTypeDAO ingredientTypeDAO;

    @Inject
    private IngredientDAO ingredientDAO;

    @Inject
    private ProductCategoryDAO productCategoryDAO;

    private IngredientType selectedIngredientType;

    private List<IngredientType> ingredientTypeList;

    public List<IngredientType> getIngredientTypeList() {
        if (ingredientTypeList == null) {
            ingredientTypeList = ingredientTypeDAO.findAll();
        }
        return ingredientTypeList;
    }

    public void openNew() {
        selectedIngredientType = new IngredientType();
    }

    @Transactional
    public void saveIngredientType() {
        if (selectedIngredientType.getId() == null) {
            ingredientTypeDAO.persist(selectedIngredientType);
        } else {
            ingredientTypeDAO.update(selectedIngredientType);
        }
        refreshIngredientTypeList();
        selectedIngredientType = null;
    }

    @Transactional
    public void deleteIngredientType(IngredientType ingredientType) {
        List<Ingredient> ingredients = ingredientDAO.findByType(ingredientType);
        for (Ingredient ingredient : ingredients) {
            ingredient.setType(null);
            ingredientDAO.update(ingredient);
        }

        List<ProductCategory> categories = productCategoryDAO.findByAddonIngredientType(ingredientType);
        for (ProductCategory category : categories) {
            category.getAddonIngredientTypes().remove(ingredientType);
            productCategoryDAO.update(category);
        }


        ingredientTypeDAO.removeById(ingredientType.getId());
        refreshIngredientTypeList();
        if (selectedIngredientType != null && selectedIngredientType.equals(ingredientType)) {
            selectedIngredientType = null;
        }
    }

    public void refreshIngredientTypeList() {
        ingredientTypeList = ingredientTypeDAO.findAll();
    }
}
