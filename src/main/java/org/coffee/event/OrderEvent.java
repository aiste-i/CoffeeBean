package org.coffee.event;

import lombok.Getter;
import org.coffee.persistence.entity.Order;

@Getter
abstract class OrderEvent {
    public final Order order;
    public OrderEvent(Order order) { this.order = order; }
}
