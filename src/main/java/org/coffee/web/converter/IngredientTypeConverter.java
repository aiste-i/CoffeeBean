package org.coffee.web.converter;

import org.coffee.persistence.dao.IngredientTypeDAO;
import org.coffee.persistence.entity.IngredientType;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;

@FacesConverter(value = "ingredientTypeConverter", managed = true)
public class IngredientTypeConverter implements Converter<IngredientType> {

    @Inject
    private IngredientTypeDAO ingredientTypeDao;

    @Override
    public IngredientType getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.isEmpty()) return null;
        try {
            Long id = Long.valueOf(value);
            IngredientType type = ingredientTypeDao.find(id);
            if (type == null) {
                throw new ConverterException("No IngredientType found with ID: " + value);
            }
            return type;
        } catch (NumberFormatException e) {
            throw new ConverterException("Invalid ingredient type ID: " + value, e);
        }
    }


    @Override
    public String getAsString(FacesContext context, UIComponent component, IngredientType value) {
        if (value == null || value.getId() == null) return "";
        return String.valueOf(value.getId());
    }
}
