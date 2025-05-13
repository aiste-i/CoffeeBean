package org.coffee.persistence.dao;

import org.coffee.persistence.entity.OrderItem;
import org.coffee.persistence.entity.User;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class OrderItemDAO extends BaseDAO<OrderItem>{
    protected OrderItemDAO() {
        super(OrderItem.class);
    }
}