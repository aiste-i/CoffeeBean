package org.coffee.persistence.dao;

import org.coffee.persistence.entity.Product;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class IngredientTypeDAO extends BaseDAO<Product> {
    public IngredientTypeDAO() {super(Product.class);}

}
