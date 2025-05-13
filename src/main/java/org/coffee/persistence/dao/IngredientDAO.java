package org.coffee.persistence.dao;

import org.coffee.persistence.entity.Ingredient;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class IngredientDAO extends BaseDAO<Ingredient> {
    public IngredientDAO() {super(Ingredient.class);}

}
