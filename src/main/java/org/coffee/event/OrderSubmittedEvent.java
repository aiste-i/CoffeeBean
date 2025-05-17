package org.coffee.event;

import org.coffee.persistence.entity.Order;

public class OrderSubmittedEvent extends OrderEvent {
    public OrderSubmittedEvent(Order order) { super(order); }
}
