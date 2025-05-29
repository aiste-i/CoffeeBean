package org.coffee.persistence.dao;

import org.coffee.persistence.entity.Ingredient;
import org.coffee.persistence.entity.IngredientType;
import org.coffee.persistence.entity.Product;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class IngredientDAO extends BaseDAO<Ingredient> {
    public IngredientDAO() {super(Ingredient.class);}

    public List<Ingredient> findByType(IngredientType type) {
        return em.createQuery(
                        "SELECT i FROM Ingredient i WHERE i.type = :type", Ingredient.class)
                .setParameter("type", type)
                .getResultList();
    }


}
