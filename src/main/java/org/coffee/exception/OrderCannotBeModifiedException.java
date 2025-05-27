package org.coffee.exception;

import org.coffee.persistence.entity.Order;
import org.coffee.persistence.entity.enums.OrderStatus;

public class OrderCannotBeModifiedException extends OrderActionException {
    public OrderCannotBeModifiedException(String message, OrderStatus currentStatus, Order conflictingOrderState) {
        super(message, currentStatus, conflictingOrderState);
    }
}
