package org.coffee.persistence.dao;

import org.coffee.persistence.entity.Order;
import org.coffee.persistence.entity.enums.OrderStatus;
import org.hibernate.Hibernate;
import org.hibernate.query.NativeQuery;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.validation.valueextraction.Unwrapping;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class OrderDAO extends BaseDAO<Order>{
    protected OrderDAO() {
        super(Order.class);
    }

    public List<Order> findByStatus(OrderStatus status) {
        if (status == null) {
            return Collections.emptyList();
        }
        TypedQuery<Order> query = em.createQuery(
                "SELECT o FROM Order o WHERE o.orderStatus = :status ORDER BY o.created ASC", Order.class);
        query.setParameter("status", status.getClass());
        return query.getResultList();
    }


    public List<Order> findActiveOrders() {
        TypedQuery<Order> query = em.createQuery(
                "SELECT o FROM Order o WHERE o.orderStatus IN (org.coffee.persistence.entity.enums.OrderStatus.PENDING, org.coffee.persistence.entity.enums.OrderStatus.ACCEPTED) ORDER BY o.created ASC", Order.class);
        return query.getResultList();
    }
}
