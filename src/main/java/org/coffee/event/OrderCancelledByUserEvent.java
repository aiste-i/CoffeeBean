package org.coffee.event;

import org.coffee.persistence.entity.Order;

public class OrderCancelledByUserEvent extends OrderEvent {
    public OrderCancelledByUserEvent(Order order) { super(order); }
}
