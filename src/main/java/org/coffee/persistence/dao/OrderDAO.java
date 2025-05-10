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
}
