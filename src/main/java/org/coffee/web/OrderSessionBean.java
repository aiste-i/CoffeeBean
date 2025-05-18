package org.coffee.web;

import lombok.Getter;
import lombok.Setter;
import org.coffee.persistence.entity.Order;
import org.coffee.persistence.entity.OrderItem;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named
@SessionScoped
@Getter
@Setter
public class OrderSessionBean implements Serializable{

    @Getter
    @Setter
    private Order currentOrder = new Order(); // not persisted yet

    @Getter
    private List<OrderItem> orderItems = new ArrayList<>();

    public void addItem(OrderItem item) {
        orderItems.add(item);
    }

    public List<OrderItem> getItems() {
        return orderItems;
    }

    public void clear() {
        orderItems.clear();
        currentOrder = new Order();
    }
}
