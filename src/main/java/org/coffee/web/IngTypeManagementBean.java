package org.coffee.web;

import lombok.Getter;
import lombok.Setter;
import org.coffee.annotations.Logged;
import org.coffee.persistence.dao.IngredientTypeDAO;
import org.coffee.persistence.entity.IngredientType;

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
    @Logged
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
    @Logged
    public void deleteIngredientType(IngredientType ingredientType) {
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
