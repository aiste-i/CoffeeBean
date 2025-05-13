package org.coffee.persistence.dao;

import org.coffee.persistence.entity.ProductCategory;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ProductCategoryDAO extends BaseDAO<ProductCategory> {
    protected ProductCategoryDAO() {
        super(ProductCategory.class);
    }
}
