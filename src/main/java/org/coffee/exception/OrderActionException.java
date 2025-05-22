package org.coffee.exception;

import lombok.Getter;
import org.coffee.persistence.entity.Order;
import org.coffee.persistence.entity.enums.OrderStatus;

@Getter
public class OrderActionException extends Exception {
    public final OrderStatus currentStatus;
    public final Order conflictingOrderState;

    public OrderActionException(String message, OrderStatus currentStatus, Order conflictingOrderState) {
        super(message);
        this.currentStatus = currentStatus;
        this.conflictingOrderState = conflictingOrderState;
    }
}