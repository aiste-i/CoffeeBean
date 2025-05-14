package org.coffee.persistence.dao;

import org.coffee.persistence.entity.Ingredient;
import org.coffee.persistence.entity.OrderItem;
import org.coffee.persistence.entity.User;

import javax.enterprise.context.ApplicationScoped;
import java.util.Collections;
import java.util.List;

@ApplicationScoped
public class OrderItemDAO extends BaseDAO<OrderItem>{
    protected OrderItemDAO() {
        super(OrderItem.class);
    }

    public List<Ingredient> getAddOnsForOrderItem(Long orderItemId) {
        OrderItem item = em.find(OrderItem.class, orderItemId);
        return item != null ? item.getAddons() : Collections.emptyList();
    }

    public void saveOrderItemWithAddOns(OrderItem item, List<Ingredient> selectedAddOns) {
        item.setAddons(selectedAddOns);
        persist(item);
    }
}