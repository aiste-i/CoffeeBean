package org.coffee.exception;

import lombok.Getter;
import org.coffee.persistence.entity.Order;

@Getter
public class OrderConflictException extends Exception {
    public final Order conflictingOrderState;
    public OrderConflictException(String message, Order conflictingOrderState) {
        super(message);
        this.conflictingOrderState = conflictingOrderState;
    }
}