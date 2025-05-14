package org.coffee.persistence.dao;

import org.coffee.persistence.entity.IngredientType;
import org.coffee.persistence.entity.ProductCategory;
import javax.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class ProductCategoryDAO extends BaseDAO<ProductCategory> {
    protected ProductCategoryDAO() {
        super(ProductCategory.class);
    }

    public List<IngredientType> findAddonIngredientTypesByCategoryId(Long categoryId) {
        ProductCategory category = em.find(ProductCategory.class, categoryId);
        return category != null ? category.getAddonIngredientTypes() : java.util.Collections.emptyList();
    }
}
