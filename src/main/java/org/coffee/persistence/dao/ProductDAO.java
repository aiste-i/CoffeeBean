package org.coffee.persistence.dao;

import org.coffee.persistence.entity.Product;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ProductDAO extends BaseDAO<Product> {
    public ProductDAO() {super(Product.class);}

}
