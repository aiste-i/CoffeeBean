package org.coffee.service.interfaces;

import org.coffee.dto.OrderCreationDto;
import org.coffee.dto.OrderModificationDto;
import org.coffee.exception.*;
import org.coffee.persistence.entity.*;
import org.coffee.persistence.entity.enums.OrderStatus;

import java.util.*;

public interface OrderService {

    Map<OrderStatus, List<Order>> getDashboardOrders();

    Order getOrderById(Long orderId) throws OrderNotFoundException;

    Order cancelOrderByUser(Long orderId, Long userId, Integer clientVersion)
            throws OrderNotFoundException, UserNotAuthorizedException,
            OrderCannotBeCancelledException, OrderConflictException;

    Order acceptOrderByEmployee(Long orderId )
            throws OrderNotFoundException, OrderActionException, OrderConflictException;

    Order cancelOrderByEmployee(Long orderId, Integer clientVersion )
            throws OrderNotFoundException, OrderActionException,
            OrderConflictException;

    Order completeOrderByEmployee(Long orderId, Integer clientVersion /*, String employeeId */)
            throws OrderNotFoundException, OrderActionException,
            OrderConflictException;

    Order createOrder(OrderCreationDto request, Long userId)
            throws UserNotFoundException, ProductNotFoundException,
            IngredientNotFoundException;

    Order modifyOrderByUser(Long orderId, OrderModificationDto modRequest, Long userId)
            throws OrderNotFoundException, OrderCannotBeModifiedException,
            OrderConflictException;

    Order getOrderDetails(Long orderId) throws OrderNotFoundException;

    public Map<OrderStatus, List<Order>> getDashboardOrdersForUser(Long userId);
}
