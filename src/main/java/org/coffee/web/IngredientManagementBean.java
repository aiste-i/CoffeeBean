package org.coffee.web;

import lombok.Getter;
import lombok.Setter;
import org.coffee.persistence.dao.IngredientDAO;
import org.coffee.persistence.entity.Ingredient;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
@Getter
@Setter
public class IngredientManagementBean implements Serializable {

    @Inject
    private IngredientDAO ingredientDao;

    private Ingredient selectedIngredient;

    private List<Ingredient> ingredientList;

    public List<Ingredient> getIngredientList() {
        if (ingredientList == null) {
            ingredientList = ingredientDao.findAll();
        }
        return ingredientList;
    }

    public void openNew() {
        selectedIngredient = new Ingredient();
    }

    public void saveIngredient() {
        if (selectedIngredient.getId() == null) {
            ingredientDao.persist(selectedIngredient);
        } else {
            ingredientDao.update(selectedIngredient);
        }
        refreshIngredientList();
        selectedIngredient = null;
    }

    public void deleteIngredient(Ingredient ingredient) {
        ingredientDao.removeById(ingredient.getId());
        refreshIngredientList();
        if (selectedIngredient != null && selectedIngredient.equals(ingredient)) {
            selectedIngredient = null;
        }
    }

    public void refreshIngredientList() {
        ingredientList = ingredientDao.findAll();
    }
}
