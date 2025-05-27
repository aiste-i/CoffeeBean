package org.coffee.persistence.dao;

import org.coffee.persistence.entity.Product;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.TypedQuery;
import java.util.List;

@ApplicationScoped
public class ProductDAO extends BaseDAO<Product> {
    public ProductDAO() {super(Product.class);}

    @Override
    public List<Product> findAll() {
        TypedQuery<Product> query = em.createQuery(
            "SELECT DISTINCT p FROM Product p " +
            "LEFT JOIN FETCH p.category c " +
            "LEFT JOIN FETCH c.addonIngredientTypes", 
            Product.class);
        return query.getResultList();
    }

    public Product findByIdWithRelationships(Long id) {
        TypedQuery<Product> query = em.createQuery(
            "SELECT DISTINCT p FROM Product p " +
            "LEFT JOIN FETCH p.category c " +
            "LEFT JOIN FETCH c.addonIngredientTypes " +
            "WHERE p.id = :id", 
            Product.class);
        query.setParameter("id", id);
        return query.getSingleResult();
    }
}
