package org.coffee.service.event;

import org.coffee.persistence.entity.Order;

public class NewOrderEvent {
    private final Order newOrder;

    public NewOrderEvent(Order newOrder) {
        this.newOrder = newOrder;
    }

    public Order getNewOrder() {
        return newOrder;
    }
}