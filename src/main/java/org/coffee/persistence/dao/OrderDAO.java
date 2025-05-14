package org.coffee.persistence.dao;

import org.coffee.persistence.entity.Order;
import org.coffee.persistence.entity.User;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class OrderDAO extends BaseDAO<Order>{
    protected OrderDAO() {
        super(Order.class);
    }

    public Order findActiveOrderByCustomerId(Long customerId) {
        List<Order> results = em.createQuery(
                        "SELECT o FROM Order o WHERE o.user.id = :customerId AND o.orderStatus = 'IN_PROGRESS'", Order.class)
                .setParameter("customerId", customerId)
                .getResultList();
        return results.isEmpty() ? null : results.get(0);
    }
}
