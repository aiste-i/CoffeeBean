package org.coffee.event;

import org.coffee.persistence.entity.Order;

public class OrderCompletedByEmployeeEvent extends OrderEvent {
    public OrderCompletedByEmployeeEvent(Order order) { super(order); }
}