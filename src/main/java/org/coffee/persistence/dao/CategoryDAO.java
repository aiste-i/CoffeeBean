package org.coffee.persistence.dao;

import org.coffee.persistence.entity.Category;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CategoryDAO extends BaseDAO<Category> {
    protected CategoryDAO() {
        super(Category.class);
    }
}
