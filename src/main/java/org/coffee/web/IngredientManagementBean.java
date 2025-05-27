package org.coffee.web;

import lombok.Getter;
import lombok.Setter;
import org.coffee.persistence.dao.IngredientDAO;
import org.coffee.persistence.dao.IngredientTypeDAO;
import org.coffee.persistence.entity.Ingredient;
import org.coffee.persistence.entity.IngredientType;

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
public class IngredientManagementBean implements Serializable {

    @Inject
    private IngredientDAO ingredientDAO;

    @Inject
    private IngredientTypeDAO ingredientTypeDAO;

    private Ingredient selectedIngredient;
    private Long ingredientTypeId;
    private List<Ingredient> ingredientList;
    private Map<Long, IngredientType> ingredientTypeDictionary;


    public void loadIngredientTypes() {
        if (ingredientTypeDictionary == null) {
            ingredientTypeDictionary = ingredientTypeDAO.findAll()
                    .stream()
                    .collect(Collectors.toMap(IngredientType::getId, Function.identity()));
        }
    }

    public List<Ingredient> getIngredientList() {
        ingredientList = ingredientDAO.findAll();
        return ingredientList;
    }

    public void openNew() {
        selectedIngredient = new Ingredient();
        loadIngredientTypes();
    }


    @Transactional
    public void saveIngredient() {
        FacesContext context = FacesContext.getCurrentInstance();

        try {
            if (selectedIngredient != null) {
                IngredientType o = ingredientTypeDictionary.get(ingredientTypeId);
                selectedIngredient.setType(o);

                if (selectedIngredient.getId() == null) {
                    ingredientDAO.persist(selectedIngredient);

                    getIngredientList();
                }
                else {
                    ingredientDAO.update(selectedIngredient);
                }
            }
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Operation failed.", "Ingredient already exists"));
        }

        getIngredientList();
        selectedIngredient = null;
    }

    public void deleteIngredient(Ingredient ingredient) {
        ingredientDAO.removeById(ingredient.getId());
        getIngredientList();
        if (selectedIngredient != null && selectedIngredient.equals(ingredient)) {
            selectedIngredient = null;
        }
    }
}
