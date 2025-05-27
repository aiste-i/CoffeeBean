package org.coffee.web;

import lombok.Getter;
import org.coffee.exception.OrderApiException;
import org.coffee.persistence.entity.Order;
import org.coffee.persistence.entity.OrderItem;
import org.coffee.persistence.entity.enums.OrderStatus;
import org.coffee.rest.OrderApiClient;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Named
@ViewScoped
public class OrderHistoryBean implements Serializable {

    @Inject
    private OrderApiClient orderApiClient;

    @Getter
    private List<Order> activeOrders;

    @Getter
    private List<Order> pastOrders;

    @Getter
    private Set<OrderItem> selectedOrderItems;

    @PostConstruct
    public void init() {
        Long userId = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("loggedInUserId");
        if (userId != null) {
            loadOrdersForUser(userId);
        } else {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "User Not Logged In", "Cannot load orders without a valid user ID."));
            initializeEmptyLists();
        }
    }

    public void loadOrdersForUser(Long userId) {
        try {
            Map<OrderStatus, List<Order>> dashboardData = orderApiClient.getDashboardOrdersByUserId(userId);
            if (dashboardData != null) {
                activeOrders = Stream.concat(
                                dashboardData.getOrDefault(OrderStatus.PENDING, Collections.emptyList()).stream(),
                                dashboardData.getOrDefault(OrderStatus.ACCEPTED, Collections.emptyList()).stream()
                        )
                        .collect(Collectors.toList());
                pastOrders = Stream.concat(
                                dashboardData.getOrDefault(OrderStatus.COMPLETED, Collections.emptyList()).stream(),
                                dashboardData.getOrDefault(OrderStatus.CANCELLED_BY_EMPLOYEE, Collections.emptyList()).stream()
                        )
                        .collect(Collectors.toList());
            } else {
                initializeEmptyLists();
            }
        } catch (OrderApiException e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Loading Orders",
                            "Could not fetch orders: " + e.getMessage()));
            initializeEmptyLists();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "System Error",
                            "An unexpected error occurred while loading orders."));
            initializeEmptyLists();
        }
    }


    private void initializeEmptyLists() {
        activeOrders = new ArrayList<>();
        pastOrders = new ArrayList<>();
    }

    public void viewOrderItemsByUser(Order order) {
        try {
            Order detailedOrder = orderApiClient.getOrderDetails(order.getId());
            this.selectedOrderItems = detailedOrder.getItems();
        } catch (OrderApiException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Failed to Retrieve Items", "Order ID: " + order.getId()));
        }
    }
}