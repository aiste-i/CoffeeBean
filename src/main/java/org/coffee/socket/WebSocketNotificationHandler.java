package org.coffee.socket;

import org.coffee.event.*;
import org.coffee.persistence.entity.Order;
import org.coffee.persistence.entity.OrderItem;
import org.coffee.util.JsonbUtil;
import org.coffee.web.UserSessionBean;
import org.hibernate.Hibernate;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Named;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class WebSocketNotificationHandler {

    @Named
    @Inject
    private UserSessionBean userSessionBean;

    private String createNotificationJson(String type, Order order) {
        // IMPORTANT: Ensure any lazy-loaded parts of Order needed by client are initialized
        // This is crucial because the transaction from OrderService is likely committed by now.
        if (order != null) {
            Hibernate.initialize(order.getItems());
            if (order.getUser() != null) {
                Hibernate.initialize(order.getUser());
            }
        }


        Map<String, Object> notification = new HashMap<>();
        notification.put("type", type);
//        notification.put("payload", order); // The Order object itself
        notification.put("orderId", order != null ? order.getId() : null);
        notification.put("timestamp", LocalDateTime.now().toString());
        return JsonbUtil.toJson(notification);
    }

    private String createNotificationPayload(String type, Order order) {
        if (order == null)
            return null;

        Hibernate.initialize(order.getItems());
        for (OrderItem item : order.getItems()) {
            Hibernate.initialize(item.getProduct());
            Hibernate.initialize(item.getAddons());
        }
        if (order.getUser() != null) {
            Hibernate.initialize(order.getUser());
        }

        Map<String, Object> notification = new HashMap<>();
        notification.put("type", type);
        notification.put("payload", order);
        notification.put("orderId", order.getId());
        notification.put("timestamp", LocalDateTime.now().toString());
        return JsonbUtil.toJson(notification);
    }

    private void notifyUser(Order order, String eventType) {
        if (order != null) {
            Hibernate.initialize(order.getUser().getId());
            String message = createNotificationPayload(eventType, order);
            if (message != null) {
                UserWebSocketEndpoint.sendToUser(order.getUser().getId().toString(), message);
            }
        }
    }

    private void notifyAllEmployees(Order order, String eventType) {
        String message = createNotificationPayload(eventType, order);
        if (message != null) {
            EmployeeWebSocketEndpoint.broadcastToEmployees(message);
        }
    }

    public void onOrderAccepted(@Observes OrderAcceptedByEmployeeEvent event) {
        notifyUser(event.getOrder(), "ORDER_ACCEPTED");
        // Also notify all employees that this order's status changed
        notifyAllEmployees(event.getOrder(), "ORDER_STATUS_UPDATED_BY_EMPLOYEE");
    }

    public void onOrderCancelledByEmployee(@Observes OrderCancelledByEmployeeEvent event) {
        notifyUser(event.getOrder(), "ORDER_CANCELLED_BY_EMPLOYEE");
        notifyAllEmployees(event.getOrder(), "ORDER_STATUS_UPDATED_BY_EMPLOYEE");
    }

    public void onOrderCompleted(@Observes OrderCompletedByEmployeeEvent event) {
        notifyUser(event.getOrder(), "ORDER_COMPLETED");
        notifyAllEmployees(event.getOrder(), "ORDER_STATUS_UPDATED_BY_EMPLOYEE");
    }


    public void onOrderCancelledByUser(@Observes OrderCancelledByUserEvent event) {
        notifyAllEmployees(event.getOrder(), "ORDER_CANCELLED_BY_USER");
        notifyUser(event.getOrder(), "ORDER_CANCELLED_CONFIRMATION");
    }

    public void onOrderSubmitted(@Observes OrderSubmittedEvent event) {
        notifyAllEmployees(event.getOrder(), "NEW_ORDER_PENDING");
        notifyUser(event.getOrder(), "ORDER_SUBMITTED_CONFIRMATION");
    }

    public void onOrderModified(@Observes OrderModifiedByUserEvent event) {
        notifyAllEmployees(event.getOrder(), "ORDER_MODIFIED_BY_USER");
        notifyUser(event.getOrder(), "ORDER_MODIFIED_CONFIRMATION");
    }
}