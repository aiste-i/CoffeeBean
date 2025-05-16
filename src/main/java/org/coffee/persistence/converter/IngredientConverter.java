package org.coffee.persistence.converter;

import org.coffee.persistence.dao.IngredientDAO;
import org.coffee.persistence.entity.Ingredient;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;

@FacesConverter(value = "ingredientConverter", managed = true)
public class IngredientConverter implements Converter<Ingredient> {

    @Inject
    private IngredientDAO ingredientDAO;

    @Override
    public Ingredient getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.isEmpty()) return null;
        return ingredientDAO.findById(Long.valueOf(value));
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Ingredient ingredient) {
        if (ingredient == null || ingredient.getId() == null) return "";
        return String.valueOf(ingredient.getId());
    }
}

