package org.coffee.rest;

import org.coffee.exception.*;
import org.coffee.persistence.entity.Order;
import org.coffee.persistence.entity.enums.OrderStatus;
import org.coffee.service.OrderService;
import org.coffee.dto.ClientMessageDto;
import org.coffee.dto.ClientConflictMessageDto;
import org.coffee.dto.OrderCreationDto;
import org.coffee.dto.OrderModificationDto;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

@Path("/orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequestScoped
public class OrderResource {

    @Inject
    private OrderService orderService;

    @Context
    private HttpServletRequest httpRequest;

    private Long getAuthenticatedUserIdFromSession() {
        HttpSession session = httpRequest.getSession(false);
        if (session != null) {
            Object userIdObj = session.getAttribute("loggedInUserId");
            if (userIdObj instanceof Long) {
                return (Long) userIdObj;
            }
            if(userIdObj instanceof String ) {
                return Long.parseLong((String) userIdObj);
            }
        }
        return null;
    }


    @POST
    public Response createOrder(@Valid OrderCreationDto request) {
        try {
            Long userId = getAuthenticatedUserIdFromSession();
            Order newOrder = orderService.createOrder(request, userId);
            return Response.status(Response.Status.CREATED).entity(newOrder).build();
        }
        catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(new ClientMessageDto(e.getMessage())).build();
        }
        catch (WebApplicationException wae) {
            return wae.getResponse();
        }
        catch (Exception e) {
            return Response.serverError().entity(new ClientMessageDto("Error creating order: " + e.getMessage())).build();
        }
    }

    @GET
    @Path("/{id}")
    public Response getOrder(@PathParam("id") Long orderId) {
        try {
            Order order = orderService.getOrderById(orderId);
            return Response.ok(order).build();
        }
        catch (OrderNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(new ClientMessageDto(e.getMessage())).build();
        }
    }

    @GET
    @Path("/{id}/details")
    public Response getOrderDetails(@PathParam("id") Long orderId) {
        try {
            Order orderDetails = orderService.getOrderDetails(orderId);
            return Response.ok(orderDetails).build();
        } catch (OrderNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Order not found for ID: " + orderId)
                    .build();
        }
    }


    @PUT
    @Path("/{id}/modify")
    public Response modifyOrderByUser(@PathParam("id") Long orderId, @Valid OrderModificationDto modRequest) {
        try {
            Long userId = getAuthenticatedUserIdFromSession();
            if(userId == null) {
                return Response.status(Response.Status.UNAUTHORIZED).entity(new ClientMessageDto("Authentication required.")).build();
            }
            Order modifiedOrder = orderService.modifyOrderByUser(orderId, modRequest, userId);
            return Response.ok(modifiedOrder).build();
        }
        catch (OrderNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(new ClientMessageDto(e.getMessage())).build();
        }
        catch (IllegalArgumentException e) {
            return Response.status(Response.Status.FORBIDDEN).entity(new ClientMessageDto(e.getMessage())).build();
        }
        catch (OrderCannotBeModifiedException e) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(new ClientConflictMessageDto("CONFLICT_STATE_CHANGED", e.getMessage(), e.getConflictingOrderState()))
                    .build();
        }
        catch (OrderConflictException e) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(new ClientConflictMessageDto("CONFLICT_STALE_DATA", e.getMessage(), e.getConflictingOrderState()))
                    .build();
        }
        catch (WebApplicationException wae) {
            return wae.getResponse();
        }
    }

    @PUT
    @Path("/{id}/cancel-by-user")
    public Response cancelOrderByUser(@PathParam("id") Long orderId, @QueryParam("version") Integer version) {
        if (version == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity(new ClientMessageDto("Client version must be provided.")).build();
        }
        try {
            Long userId = getAuthenticatedUserIdFromSession();
            Order cancelledOrder = orderService.cancelOrderByUser(orderId, userId, version);
            return Response.ok(cancelledOrder).build();
        }
        catch (OrderNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(new ClientMessageDto(e.getMessage())).build();
        }
        catch (UserNotAuthorizedException e) {
            return Response.status(Response.Status.FORBIDDEN).entity(new ClientMessageDto(e.getMessage())).build();
        }
        catch (OrderConflictException e) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(new ClientConflictMessageDto("CONFLICT_STALE_DATA", e.getMessage(), e.getConflictingOrderState()))
                    .build();
        }
        catch (WebApplicationException wae) {
            return wae.getResponse();
        }
        catch ( OrderCannotBeCancelledException e) {
            throw new RuntimeException(e);
        }

    }

    @PUT
    @Path("/{id}/accept-by-employee")
    public Response acceptOrderByEmployee(@PathParam("id") Long orderId) {
        try {
            Order acceptedOrder = orderService.acceptOrderByEmployee(orderId);
            return Response.ok(acceptedOrder).build();
        }
        catch (OrderNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(new ClientMessageDto(e.getMessage())).build();
        }
        catch (OrderActionException e) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(new ClientConflictMessageDto("CONFLICT_CANNOT_ACCEPT", e.getMessage(), e.getConflictingOrderState()))
                    .build();
        }
        catch (OrderConflictException e) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(new ClientConflictMessageDto("CONFLICT_STALE_DATA", e.getMessage(), e.getConflictingOrderState()))
                    .build();
        }
    }

    @PUT
    @Path("/{id}/cancel-by-employee")
    public Response cancelOrderByEmployee(@PathParam("id") Long orderId, @QueryParam("version") Integer version) {
        if (version == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity(new ClientMessageDto("Client version must be provided.")).build();
        }
        try {
            Order cancelledOrder = orderService.cancelOrderByEmployee(orderId, version);
            return Response.ok(cancelledOrder).build();
        }
        catch (OrderNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(new ClientMessageDto(e.getMessage())).build();
        }
        catch (OrderActionException e) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(new ClientConflictMessageDto("CONFLICT_CANNOT_CANCEL", e.getMessage(), e.getConflictingOrderState()))
                    .build();
        }
        catch (OrderConflictException e) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(new ClientConflictMessageDto("CONFLICT_STALE_DATA", e.getMessage(), e.getConflictingOrderState()))
                    .build();
        }
    }

    @GET
    @Path("/dashboard")
    public Response getDashboardOrdersForEmployee() {
        try {
            Map<OrderStatus, List<Order>> dashboardOrders = orderService.getDashboardOrders();
            return Response.ok(dashboardOrders).build();
        }
        catch (Exception e) {
            return Response.serverError().entity(new ClientMessageDto("Error fetching dashboard orders: " + e.getMessage())).build();
        }
    }

    @PUT
    @Path("/{id}/complete-by-employee")
    public Response completeOrderByEmployee(@PathParam("id") Long orderId, @QueryParam("version") Integer version) {
        if (version == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity(new ClientMessageDto("Client version must be provided.")).build();
        }
        try {
            Order completedOrder = orderService.completeOrderByEmployee(orderId, version);
            return Response.ok(completedOrder).build();
        }
        catch (OrderNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(new ClientMessageDto(e.getMessage())).build();
        }
        catch (OrderActionException e) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(new ClientConflictMessageDto("CONFLICT_CANNOT_COMPLETE", e.getMessage(), e.getConflictingOrderState()))
                    .build();
        }
        catch (OrderConflictException e) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(new ClientConflictMessageDto("CONFLICT_STALE_DATA", e.getMessage(), e.getConflictingOrderState()))
                    .build();
        }
    }
}
