package org.coffee.web;

import lombok.Getter;
import lombok.Setter;
import org.coffee.exception.OrderApiException;
import org.coffee.persistence.entity.Order;
import org.coffee.persistence.entity.enums.OrderStatus;
import org.coffee.rest.OrderApiClient;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Named
@ViewScoped
public class EmployeeDashboardBean implements Serializable {

    @Inject
    private OrderApiClient orderApiClient;

    private List<Order> pendingOrders;
    private List<Order> acceptedOrders;

    @Setter
    @Getter
    private Order selectedOrder;

    @PostConstruct
    public void init() {
        loadOrders();
    }

    public void loadOrders() {
        try {
            Map<OrderStatus, List<Order>> dashboardData = orderApiClient.getDashboardOrders();
            if (dashboardData != null) {
                pendingOrders = dashboardData.getOrDefault(OrderStatus.PENDING, Collections.emptyList());
                acceptedOrders = dashboardData.getOrDefault(OrderStatus.ACCEPTED, Collections.emptyList());

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
        pendingOrders = new ArrayList<>();
        acceptedOrders = new ArrayList<>();
    }

    private void handleActionError(String actionName, Exception e, Long orderId) {
        String detail = e.getMessage();
        if (e instanceof OrderApiException) {
            OrderApiException apiEx = (OrderApiException) e;
            if (apiEx.getHttpStatusCode() == 409) {
                detail = "Action failed due to a conflict or state change. Please refresh. Details: " + apiEx.getMessage();
                loadOrders();
            }
        }
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, actionName + " Failed" + (orderId != null ? " (Order ID: " + orderId + ")" : ""), detail));
    }

    public void acceptOrder(Order order) {
        if (order == null) return;
        try {
            orderApiClient.acceptOrderAsEmployee(order.getId());
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Order Accepted", "Order ID: " + order.getId()));
            loadOrders();
        } catch (Exception e) {
            handleActionError("Accept Order", e, order.getId());
        }
    }

    public void startProcessingOrder(Order order) {
        if (order == null) return;
        try {
            orderApiClient.processOrderAsEmployee(order.getId());
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Order Processing", "Order ID: " + order.getId()));
            loadOrders();
        } catch (Exception e) {
            handleActionError("Start Processing", e, order.getId());
        }
    }

    public void completeOrder(Order order) {
        if (order == null) return;
        try {
            orderApiClient.completeOrderAsEmployee(order.getId(), order.getVersion());
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Order Completed", "Order ID: " + order.getId()));
            loadOrders();
        } catch (Exception e) {
            handleActionError("Complete Order", e, order.getId());
        }
    }

    public void cancelOrder(Order order) {
        if (order == null) return;
        try {
            orderApiClient.cancelOrderAsEmployee(order.getId(), order.getVersion());
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Order Cancelled", "Order ID: " + order.getId()));
            loadOrders();
        } catch (Exception e) {
            handleActionError("Cancel Order", e, order.getId());
        }
    }

    public List<Order> getPendingOrders() { return pendingOrders == null ? Collections.emptyList() : pendingOrders; }
    public List<Order> getAcceptedOrders() { return acceptedOrders == null ? Collections.emptyList() : acceptedOrders; }

}