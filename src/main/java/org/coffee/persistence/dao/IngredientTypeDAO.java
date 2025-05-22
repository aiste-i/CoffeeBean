package org.coffee.persistence.dao;

import org.coffee.persistence.entity.IngredientType;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class IngredientTypeDAO extends BaseDAO<IngredientType> {
    public IngredientTypeDAO() {super(IngredientType.class);}

}
