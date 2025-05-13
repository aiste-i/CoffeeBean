package org.coffee.service;

import org.coffee.persistence.dao.OrderDAO;
import org.coffee.persistence.entity.Order;
import org.coffee.service.event.NewOrderEvent;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless // Or @ApplicationScoped
public class OrderService {

    @Inject
    private OrderDAO orderDAO;

    @Inject
    private Event<NewOrderEvent> newOrderEventNotifier; // Inject event producer

    public Order placeOrder(Order order) {
        try {
            orderDAO.persist(order);

            newOrderEventNotifier.fire(new NewOrderEvent(order));
            System.out.println("Fired NewOrderForEmployeeEvent for Order ID: " + order.getId());

            return order;
        } catch (Exception e) {
            System.err.println("Failed to persist order or fire event: " + e.getMessage());
            throw new IllegalStateException("Failed to save order to the system. Please try again.", e);
        }
    }
}
