package org.coffee.rest;

import org.coffee.dto.ClientConflictMessageDto;
import org.coffee.dto.ClientMessageDto;
import org.coffee.dto.OrderCreationDto;
import org.coffee.exception.OrderApiException;
import org.coffee.persistence.entity.Order;
import org.coffee.persistence.entity.enums.OrderStatus;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class OrderApiClient {

    private final String apiBaseUrl = "http://localhost:9080/coffee-1.0-SNAPSHOT/api";

    private Client client;

    private WebTarget baseTarget;

    @PostConstruct
    public void init() {
        this.client = ClientBuilder.newClient();
        this.baseTarget = client.target(apiBaseUrl);
    }

    @PreDestroy
    public void destroy() {
        if (this.client != null) {
            this.client.close();
        }
    }

    public Order createOrder(OrderCreationDto dto) throws OrderApiException {
        WebTarget createOrderTarget = baseTarget.path("orders");
        try (Response response = createOrderTarget
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(dto, MediaType.APPLICATION_JSON))) {

            return handleResponse(response, Order.class, "Create Order");
        } catch (ProcessingException e) {
            throw new OrderApiException("Network or processing error during create order: " + e.getMessage(), 0, e);
        }
    }


    public Order getOrderById(Long orderId) throws OrderApiException {
        WebTarget getOrderTarget = baseTarget.path("orders").path(String.valueOf(orderId));
        try (Response response = getOrderTarget
                .request(MediaType.APPLICATION_JSON)
                .get()) {
            return handleResponse(response, Order.class, "Get Order by ID");
        } catch (ProcessingException e) {
            throw new OrderApiException("Network or processing error during get order: " + e.getMessage(), 0, e);
        }
    }

    public Order getOrderDetails(Long orderId) throws OrderApiException {
        WebTarget getOrderDetailsTarget = baseTarget.path("orders").path(String.valueOf(orderId)).path("details");
        try (Response response = getOrderDetailsTarget
                .request(MediaType.APPLICATION_JSON)
                .get()) {
            return handleResponse(response, Order.class, "Get Order Details");
        } catch (ProcessingException e) {
            throw new OrderApiException("Network or processing error during get order details: " + e.getMessage(), 0, e);
        }
    }

    public List<Order> getAllOrders() throws OrderApiException {
        WebTarget getAllOrdersTarget = baseTarget.path("orders");
        try (Response response = getAllOrdersTarget
                .request(MediaType.APPLICATION_JSON)
                .get()) {

            if (response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                if (response.hasEntity()) {
                    return response.readEntity(new GenericType<List<Order>>() {});
                }
                return java.util.Collections.emptyList();
            } else {
                handleErrorResponse(response, "Get All Orders");
                return null;
            }
        } catch (ProcessingException e) {
            throw new OrderApiException("Network or processing error during get all orders: " + e.getMessage(), 0, e);
        }
    }

    public Map<OrderStatus, List<Order>> getDashboardOrders() throws OrderApiException {
        WebTarget target = baseTarget.path("orders").path("dashboard");
        try (Response response = target
                .request(MediaType.APPLICATION_JSON)
                .get()) {

            if (response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                if (response.hasEntity()) {
                    return response.readEntity(new GenericType<Map<OrderStatus, List<Order>>>() {});
                }
                return java.util.Collections.emptyMap();
            } else {
                handleErrorResponse(response, "Get Dashboard Orders");
                return null;
            }
        } catch (ProcessingException e) {
            throw new OrderApiException("Network or processing error during get dashboard orders: " + e.getMessage(), 0, e);
        }
    }


    public Order acceptOrderAsEmployee(Long orderId) throws OrderApiException {
        WebTarget target = baseTarget.path("orders").path(String.valueOf(orderId)).path("accept-by-employee");
        try (Response response = target
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.json(null))) {
            return handleResponse(response, Order.class, "Accept Order (Employee)");
        } catch (ProcessingException e) {
            throw new OrderApiException("Network or processing error accepting order: " + e.getMessage(), 0, e);
        }
    }



    public Order completeOrderAsEmployee(Long orderId, Integer version) throws OrderApiException {
        WebTarget target = baseTarget.path("orders").path(String.valueOf(orderId)).path("complete-by-employee")
                .queryParam("version", version);
        try (Response response = target
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.json(null))) {
            return handleResponse(response, Order.class, "Complete Order (Employee)");
        } catch (ProcessingException e) {
            throw new OrderApiException("Network or processing error completing order: " + e.getMessage(), 0, e);
        }
    }

    public Order cancelOrderAsEmployee(Long orderId, Integer version) throws OrderApiException {
        WebTarget target = baseTarget.path("orders").path(String.valueOf(orderId)).path("cancel-by-employee")
                .queryParam("version", version);
        System.out.println("Cancel API Call - Order ID: " + orderId + ", Version: " + version);

        try (Response response = target
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.json(null))) {
            return handleResponse(response, Order.class, "Cancel Order (Employee)");
        } catch (ProcessingException e) {
            throw new OrderApiException("Network or processing error cancelling order by employee: " + e.getMessage(), 0, e);
        }
    }

    private <T> T handleResponse(Response response, Class<T> responseType, String actionName) throws OrderApiException {
        int statusCode = response.getStatus();
        Response.Status.Family family = response.getStatusInfo().getFamily();

        if (family == Response.Status.Family.SUCCESSFUL) {
            if (statusCode == Response.Status.NO_CONTENT.getStatusCode() || responseType == null || responseType.equals(Void.class)) {
                return null;
            }
            if (response.hasEntity()) {
                try {
                    return response.readEntity(responseType);
                } catch (ProcessingException e) {
                    throw new OrderApiException(actionName + " succeeded but failed to read response entity: " + e.getMessage(), statusCode, e);
                }
            } else {
                return null;
            }
        } else {
            handleErrorResponse(response, actionName);
            return null;
        }
    }

    private void handleErrorResponse(Response response, String actionName) throws OrderApiException {
        int statusCode = response.getStatus();
        String errorDetails = "No additional details available from server.";

        if (response.hasEntity()) {
            try {
                if (statusCode == Response.Status.CONFLICT.getStatusCode()) {
                    ClientConflictMessageDto conflictMsg = response.readEntity(ClientConflictMessageDto.class);
                    errorDetails = conflictMsg.getMessage();
                } else if (statusCode == Response.Status.BAD_REQUEST.getStatusCode() ||
                        statusCode == Response.Status.NOT_FOUND.getStatusCode() ||
                        statusCode == Response.Status.FORBIDDEN.getStatusCode()) {
                    ClientMessageDto clientMsg = response.readEntity(ClientMessageDto.class);
                    errorDetails = clientMsg.getMessage();
                } else {
                    errorDetails = response.readEntity(String.class);
                }
            } catch (ProcessingException e) {
                errorDetails = "Failed to read error response from server. Status: " + statusCode;
            } catch (Exception e) {
                errorDetails = "Unexpected error reading error response from server. Status: " + statusCode;
            }
        }

        String errorMessage = actionName + " failed with HTTP " + statusCode + ". " + errorDetails;
        throw new OrderApiException(errorMessage, statusCode);
    }
}