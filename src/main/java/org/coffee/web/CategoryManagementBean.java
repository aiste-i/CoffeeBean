package org.coffee.web;

import lombok.Getter;
import lombok.Setter;
import org.coffee.persistence.dao.ProductCategoryDAO;
import org.coffee.persistence.entity.ProductCategory;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
@Getter
@Setter
public class CategoryManagementBean implements Serializable {

    @Inject
    private ProductCategoryDAO categoryDAO;

    private ProductCategory selectedCategory;

    private List<ProductCategory> categoryList;

    public List<ProductCategory> getCategoryList() {
        if (categoryList == null) {
            categoryList = categoryDAO.findAll();
        }
        return categoryList;
    }

    public void openNew() {
        selectedCategory = new ProductCategory();
    }

    public void saveCategory() {
        if (selectedCategory.getId() == null) {
            categoryDAO.persist(selectedCategory);
        } else {
            categoryDAO.update(selectedCategory);
        }
        refreshCategoryList();
        selectedCategory = null;
    }

    public void deleteCategory(ProductCategory category) {
        categoryDAO.removeById(category.getId());
        refreshCategoryList();
        if (selectedCategory != null && selectedCategory.equals(category)) {
            selectedCategory = null;
        }
    }

    public void refreshCategoryList() {
        categoryList = categoryDAO.findAll();
    }
}
