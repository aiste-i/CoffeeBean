package org.coffee.event;

import org.coffee.persistence.entity.Order;

public class OrderModifiedByUserEvent extends OrderEvent {
    public OrderModifiedByUserEvent(Order order) { super(order); }
}