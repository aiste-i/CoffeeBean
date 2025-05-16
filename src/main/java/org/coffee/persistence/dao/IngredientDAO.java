package org.coffee.persistence.dao;

import org.coffee.persistence.entity.Ingredient;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class IngredientDAO extends BaseDAO<Ingredient> {
    public IngredientDAO() {super(Ingredient.class);}

    public Ingredient findById(Long id) {
        return em.find(Ingredient.class, id);
    }

    public List<Ingredient> findByTypeId(Long typeId) {
        return em.createQuery(
                        "SELECT i FROM Ingredient i WHERE i.type.id = :typeId", Ingredient.class)
                .setParameter("typeId", typeId)
                .getResultList();
    }

    public List<Ingredient> findAddOnsForCategory(Long categoryId) {
        return em.createQuery(
                        "SELECT i FROM Ingredient i WHERE i.type IN (" +
                                "SELECT t FROM ProductCategory c JOIN c.addonIngredientTypes t WHERE c.id = :categoryId)",
                        Ingredient.class)
                .setParameter("categoryId", categoryId)
                .getResultList();
    }
}
