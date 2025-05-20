package org.coffee.exception;

import org.coffee.persistence.entity.Order;
import org.coffee.persistence.entity.enums.OrderStatus;

public class OrderCannotBeCancelledException extends OrderActionException {
    public OrderCannotBeCancelledException(String message, OrderStatus currentStatus, Order conflictingOrderState) {
        super(message, currentStatus, conflictingOrderState);
    }
}