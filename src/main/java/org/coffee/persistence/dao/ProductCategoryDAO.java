package org.coffee.persistence.dao;

import org.coffee.persistence.entity.IngredientType;
import org.coffee.persistence.entity.ProductCategory;
import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.List;

import static org.coffee.constants.Constants.PERSISTENCE_UNIT;

@ApplicationScoped
public class ProductCategoryDAO extends BaseDAO<ProductCategory> {

    @PersistenceContext(unitName = PERSISTENCE_UNIT)
    protected EntityManager em;

    protected ProductCategoryDAO() {
        super(ProductCategory.class);
    }

    public ProductCategory findByIdWithAddonIngredientTypes(Long id) {
        return em.createQuery(
                        "SELECT c FROM ProductCategory c LEFT JOIN FETCH c.addonIngredientTypes WHERE c.id = :id",
                        ProductCategory.class)
                .setParameter("id", id)
                .getSingleResult();
    }
    public List<ProductCategory> findByAddonIngredientType(IngredientType type) {
        return em.createQuery(
                        "SELECT c FROM ProductCategory c JOIN c.addonIngredientTypes t WHERE t = :type", ProductCategory.class)
                .setParameter("type", type)
                .getResultList();
    }

}
