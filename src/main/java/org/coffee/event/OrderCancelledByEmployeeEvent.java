package org.coffee.event;

import org.coffee.persistence.entity.Order;

public class OrderCancelledByEmployeeEvent extends OrderEvent {
    public OrderCancelledByEmployeeEvent(Order order) { super(order); }
}