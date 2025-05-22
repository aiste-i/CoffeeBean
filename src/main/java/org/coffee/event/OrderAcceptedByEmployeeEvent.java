package org.coffee.event;

import org.coffee.persistence.entity.Order;

public class OrderAcceptedByEmployeeEvent extends OrderEvent {
    public OrderAcceptedByEmployeeEvent(Order order ) {
        super(order);
    }
}
