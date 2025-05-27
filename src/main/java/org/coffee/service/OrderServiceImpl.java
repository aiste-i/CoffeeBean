package org.coffee.service;

import org.coffee.annotations.Logged;
import org.coffee.event.*;
import org.coffee.exception.*;
import org.coffee.persistence.dao.*;
import org.coffee.persistence.entity.*;
import org.coffee.persistence.entity.enums.OrderStatus;
import org.coffee.dto.OrderItemDto;
import org.coffee.dto.OrderCreationDto;
import org.coffee.dto.OrderModificationDto;
import org.coffee.service.interfaces.OrderService;
import org.hibernate.Hibernate;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.OptimisticLockException;
import javax.transaction.Transactional;
import java.util.*;

@Stateless
public class OrderServiceImpl implements OrderService {

    @Inject
    private OrderDAO orderDAO;

    @Inject
    private UserDAO userDAO;

    @Inject
    private ProductDAO productDAO;

    @Inject
    private IngredientDAO ingredientDAO;

    @Inject
    Event<OrderSubmittedEvent> orderSubmittedEvent;

    @Inject
    Event<OrderAcceptedByEmployeeEvent> orderAcceptedEvent;

    @Inject
    Event<OrderCancelledByUserEvent> orderCancelledByUserEvent;

    @Inject
    Event<OrderCancelledByEmployeeEvent> orderCancelledByEmployeeEvent;

    @Inject
    Event<OrderModifiedByUserEvent> orderModifiedEvent;

    @Inject
    Event<OrderCompletedByEmployeeEvent> orderCompletedEvent;

    @Transactional(Transactional.TxType.REQUIRED)
    public Map<OrderStatus, List<Order>> getDashboardOrders() {
        List<Order> activeOrders = orderDAO.findActiveOrders();

        Map<OrderStatus, List<Order>> dashboardOrders = new HashMap<>();
        dashboardOrders.put(OrderStatus.PENDING, new ArrayList<>());
        dashboardOrders.put(OrderStatus.ACCEPTED, new ArrayList<>());

        for (Order order : activeOrders) {
            Order initializedOrder = initializeOrder(order);
            if (initializedOrder != null && dashboardOrders.containsKey(initializedOrder.getOrderStatus())) {
                dashboardOrders.get(initializedOrder.getOrderStatus()).add(initializedOrder);
            }
        }
        return dashboardOrders;
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public Map<OrderStatus, List<Order>> getDashboardOrdersForUser(Long userId) {
        if (userId == null) {
            return Collections.emptyMap();
        }

        List<Order> userOrders = orderDAO.findOrdersByUserId(userId);

        Map<OrderStatus, List<Order>> dashboardOrders = new HashMap<>();
        dashboardOrders.put(OrderStatus.PENDING, new ArrayList<>());
        dashboardOrders.put(OrderStatus.ACCEPTED, new ArrayList<>());
        dashboardOrders.put(OrderStatus.CANCELLED_BY_EMPLOYEE, new ArrayList<>());
        dashboardOrders.put(OrderStatus.COMPLETED, new ArrayList<>());

        for (Order order : userOrders) {
            Order initializedOrder = initializeOrder(order);
            if (initializedOrder != null && dashboardOrders.containsKey(initializedOrder.getOrderStatus())) {
                dashboardOrders.get(initializedOrder.getOrderStatus()).add(initializedOrder);
            }
        }
        return dashboardOrders;


    }

    public Order getOrderById(Long orderId) throws OrderNotFoundException {
        Order order = orderDAO.find(orderId);
        if (order == null) {
            throw new OrderNotFoundException("Order not found with ID: " + orderId);
        }
        return initializeOrder(order);
    }

    @Transactional(Transactional.TxType.REQUIRED)
    @Logged
    public Order cancelOrderByUser(Long orderId, Long userId, Integer clientVersion)
            throws OrderNotFoundException, UserNotAuthorizedException, OrderCannotBeCancelledException, OrderConflictException {
        try {
            Order order = findOrderByIdAndEnsureInitialized(orderId);

            if (order.getUser() == null || !order.getUser().getId().equals(userId)) {
                throw new UserNotAuthorizedException("User not authorized to cancel this order.");
            }
            if (!(order.getOrderStatus() == OrderStatus.PENDING || order.getOrderStatus() == OrderStatus.ACCEPTED)) {
                throw new OrderCannotBeCancelledException(
                        "Order can no longer be cancelled by user. Current status: " + order.getOrderStatus(),
                        order.getOrderStatus(), order);
            }
            if (clientVersion != null && !order.getVersion().equals(clientVersion)) {
                throw new OrderConflictException("The order was updated since you last viewed it. Please refresh and try again.", order);
            }

            order.setOrderStatus(OrderStatus.CANCELLED_BY_CUSTOMER);
            Order mergedOrder = orderDAO.update(order);
            orderDAO.flush();

            orderCancelledByUserEvent.fire(new OrderCancelledByUserEvent(initializeOrder(mergedOrder)));
            return mergedOrder;
        } catch (OptimisticLockException ole) {
            Order currentDbOrder = findOrderByIdAndEnsureInitialized(orderId);
            throw new OrderConflictException(
                    "Could not cancel the order as its state changed concurrently. Please refresh.",
                    currentDbOrder);
        }
    }

    @Transactional(Transactional.TxType.REQUIRED)
    @Logged
    public Order acceptOrderByEmployee(Long orderId )
            throws OrderNotFoundException, OrderActionException, OrderConflictException {
        try {
            Order order = findOrderByIdAndEnsureInitialized(orderId);

            if (order.getOrderStatus() != OrderStatus.PENDING) {
                throw new OrderActionException(
                        "Order cannot be accepted. Current status: " + order.getOrderStatus(),
                        order.getOrderStatus(), order);
            }

            order.setOrderStatus(OrderStatus.ACCEPTED);
            Order mergedOrder = orderDAO.update(order);
            orderDAO.flush();

            orderAcceptedEvent.fire(new OrderAcceptedByEmployeeEvent(initializeOrder(mergedOrder)));
            return mergedOrder;
        } catch (OptimisticLockException ole) {
            Order currentDbOrder = findOrderByIdAndEnsureInitialized(orderId);
            throw new OrderConflictException(
                    "Could not accept the order as its state changed concurrently. Please refresh.",
                    currentDbOrder);
        }
    }

    @Transactional(Transactional.TxType.REQUIRED)
    @Logged
    public Order cancelOrderByEmployee(Long orderId, Integer clientVersion )
            throws OrderNotFoundException, OrderActionException, OrderConflictException {
        try {
            Order order = findOrderByIdAndEnsureInitialized(orderId);
            if (order.getOrderStatus() == OrderStatus.COMPLETED ||
                    order.getOrderStatus() == OrderStatus.CANCELLED_BY_CUSTOMER||
                    order.getOrderStatus() == OrderStatus.CANCELLED_BY_EMPLOYEE) {
                throw new OrderActionException(
                        "Order cannot be cancelled by employee. Current status: " + order.getOrderStatus(),
                        order.getOrderStatus(), order);
            }
            if (clientVersion != null && !order.getVersion().equals(clientVersion)) {
                throw new OrderConflictException("The order was updated since you last viewed it. Please refresh.", order);
            }

            order.setOrderStatus(OrderStatus.CANCELLED_BY_EMPLOYEE);
            Order mergedOrder = orderDAO.update(order);
            orderDAO.flush();

            orderCancelledByEmployeeEvent.fire(new OrderCancelledByEmployeeEvent(initializeOrder(mergedOrder)));
            return mergedOrder;
        } catch (OptimisticLockException ole) {
            Order currentDbOrder = findOrderByIdAndEnsureInitialized(orderId);
            throw new OrderConflictException(
                    "Could not cancel the order as its state changed concurrently. Please refresh.",
                    currentDbOrder);
        }
    }

    @Transactional(Transactional.TxType.REQUIRED)
    @Logged
    public Order completeOrderByEmployee(Long orderId, Integer clientVersion /*, String employeeId */)
            throws OrderNotFoundException, OrderActionException, OrderConflictException {
        try {
            Order order = findOrderByIdAndEnsureInitialized(orderId);

            if (order.getOrderStatus() != OrderStatus.ACCEPTED ) {
                throw new OrderActionException(
                        "Order cannot be completed. Current status: " + order.getOrderStatus(),
                        order.getOrderStatus(), order);
            }
            if (clientVersion != null && !order.getVersion().equals(clientVersion)) {
                throw new OrderConflictException("The order was updated since you last viewed it. Please refresh.", order);
            }

            order.setOrderStatus(OrderStatus.COMPLETED);
            order.setCompleted(java.time.LocalDateTime.now());
            Order mergedOrder = orderDAO.update(order);
            orderDAO.flush();

            orderCompletedEvent.fire(new OrderCompletedByEmployeeEvent(initializeOrder(mergedOrder)));
            return mergedOrder;
        } catch (OptimisticLockException ole) {
            Order currentDbOrder = findOrderByIdAndEnsureInitialized(orderId);
            throw new OrderConflictException(
                    "Could not complete the order as its state changed concurrently. Please refresh.",
                    currentDbOrder);
        }
    }

    @Transactional(Transactional.TxType.REQUIRED)
    @Logged
    public Order createOrder(OrderCreationDto request, Long userId)
            throws UserNotFoundException, ProductNotFoundException, IngredientNotFoundException {

        if (request.getItems() == null) {
            System.err.println("OrderService.createOrder: CRITICAL - request.getItems() is NULL. Cannot create order items.");
            throw new IllegalArgumentException("Order creation request must contain an items list.");
        }

        User user = null;
        if (userId != null) {
            user = userDAO.find(userId);
            if (user == null) {
                throw new UserNotFoundException("User with ID " + userId + " not found.");
            }
        }

        Order orderEntity = new Order();
        orderEntity.setName(request.getOrderName());
        orderEntity.setUser(user);
        orderEntity.setCustomerName(request.getCustomerName());
        orderEntity.setCustomerEmail(request.getCustomerEmail() != null ? request.getCustomerEmail() : (user != null ? user.getEmail() : null));
        orderEntity.setOrderStatus(OrderStatus.NOT_PAYED);

        for (OrderItemDto itemDto : request.getItems()) {
            if (itemDto.getProductId() == null) {
                throw new ProductNotFoundException("Item DTO contains a null ProductId.");
            }

            Product product = null;
            try {
                product = productDAO.find(itemDto.getProductId());
            } catch (Exception e) {
                throw new ProductNotFoundException("Error finding product with ID " + itemDto.getProductId() + ". Cause: " + e.getMessage());
            }

            if (product == null) {
                throw new ProductNotFoundException("Product not found with ID: " + itemDto.getProductId());
            }

            OrderItem orderItemEntity = new OrderItem();
            orderItemEntity.setProduct(product);
            orderItemEntity.setName(product.getName());
            orderItemEntity.setSpecialRequirements(itemDto.getSpecialRequirements());
            orderItemEntity.setQuantity(itemDto.getQuantity());
            System.out.println("OrderService.createOrder: OrderItem entity populated with Product: " + orderItemEntity.getProduct().getName());

            if (itemDto.getAddonIngredientIds() != null && !itemDto.getAddonIngredientIds().isEmpty()) {
                Set<Ingredient> addons = new HashSet<>();
                for (Long addonId : itemDto.getAddonIngredientIds()) {
                    if (addonId == null) {
                        continue;
                    }
                    Ingredient addon = null;
                    try {
                        addon = ingredientDAO.find(addonId);
                    } catch (Exception e) {
                        throw new IngredientNotFoundException("Error finding addon with ID " + addonId + ": " + e.getMessage());
                    }

                    if (addon == null || addon.isDeleted()) {
                        throw new IngredientNotFoundException("Addon ingredient not found or unavailable: ID " + addonId);
                    }
                    addons.add(addon);
                }
                orderItemEntity.setAddons(addons);
                System.out.println("OrderService.createOrder: Associated " + addons.size() + " Ingredient entities to OrderItem: " + orderItemEntity.getName());
            } else {
                System.out.println("OrderService.createOrder: No addon IDs specified for OrderItem: " + orderItemEntity.getName());
            }

            orderEntity.addItem(orderItemEntity);
        }
        orderDAO.persist(orderEntity);
        return orderEntity;
    }


    @Transactional(Transactional.TxType.REQUIRED)
    @Logged
    public Order modifyOrderByUser(Long orderId, OrderModificationDto modRequest, Long userId)
            throws OrderNotFoundException, OrderCannotBeModifiedException,
            OrderConflictException {
        try {
            Order order = findOrderByIdAndEnsureInitialized(orderId);

            if (order.getUser() == null || !order.getUser().getId().equals(userId)) {
                throw new IllegalArgumentException("User not authorized to modify this order.");
            }
            if (order.getOrderStatus() != OrderStatus.PENDING) {
                throw new OrderCannotBeModifiedException(
                        "Order can no longer be modified. Current status: " + order.getOrderStatus(),
                        order.getOrderStatus(), order);
            }
            if (!order.getVersion().equals(modRequest.getVersion())) {
                throw new OrderConflictException(
                        "The order was updated since you last viewed it. Please refresh.",
                        order);
            }

            if (modRequest.getCustomerName() != null) {
                order.setCustomerName(modRequest.getCustomerName());
            }
            if (modRequest.getCustomerEmail() != null) {
                order.setCustomerEmail(modRequest.getCustomerEmail());
            }

            order.getItems().clear();
            orderDAO.flush();

            for (OrderItemDto itemDto : modRequest.getUpdatedItems()) {
                Product product = productDAO.find(itemDto.getProductId());
                if (product == null) {
                    throw new IllegalArgumentException("Product not found with ID: " + itemDto.getProductId());
                }

                OrderItem orderItem = new OrderItem();
                orderItem.setProduct(product);
                orderItem.setName(product.getName());
                orderItem.setSpecialRequirements(itemDto.getSpecialRequirements());
                orderItem.setQuantity(itemDto.getQuantity());

                if (itemDto.getAddonIngredientIds() != null && !itemDto.getAddonIngredientIds().isEmpty()) {
                    Set<Ingredient> addons = new HashSet<>();
                    for (Long addonId : itemDto.getAddonIngredientIds()) {
                        Ingredient addon = ingredientDAO.find(addonId);
                        if (addon == null || addon.isDeleted()) {
                            throw new IllegalArgumentException("Addon ingredient not found or is unavailable: ID " + addonId);
                        }
                        addons.add(addon);
                    }
                    orderItem.setAddons(addons);
                }
                order.addItem(orderItem);
            }

            order.setTotalPrice(order.calculateTotalPrice());
            Order mergedOrder = orderDAO.update(order);
            orderDAO.flush();

            orderModifiedEvent.fire(new OrderModifiedByUserEvent(initializeOrder(mergedOrder)));
            return mergedOrder;

        } catch (OptimisticLockException ole) {
            Order currentDbOrder = findOrderByIdAndEnsureInitialized(orderId);
            throw new OrderConflictException(
                    "Your modification could not be completed because the order was updated by someone else.",
                    currentDbOrder);
        }
    }

    private Order findOrderByIdAndEnsureInitialized(Long orderId) throws OrderNotFoundException {
        Order order = orderDAO.find(orderId);
        if (order == null) {
            throw new OrderNotFoundException("Order not found with ID: " + orderId);
        }
        return initializeOrder(order);
    }

    @Transactional(Transactional.TxType.REQUIRED)
    private Order initializeOrder(Order order) {
        if (order == null)
            return null;
        Hibernate.initialize(order.getItems());
        for (OrderItem item : order.getItems()) {
            Hibernate.initialize(item.getProduct());
            if (item.getProduct() != null) {
                Hibernate.initialize(item.getProduct().getCategory());
            }
            Hibernate.initialize(item.getAddons());
        }
        if (order.getUser() != null) {
            Hibernate.initialize(order.getUser());
        }
         Hibernate.initialize(order.getPayments());
        return order;
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public Order getOrderDetails(Long orderId) throws OrderNotFoundException {
        Order order = orderDAO.findOrderWithItems(orderId);
        if (order == null) {
            throw new OrderNotFoundException("Order not found for ID: " + orderId);
        }
        return order;
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public Order updateOrder(Long orderId)
            throws OrderNotFoundException, OrderActionException, OrderConflictException {
        try {
            Order order = findOrderByIdAndEnsureInitialized(orderId);

            if (order.getOrderStatus() != OrderStatus.NOT_PAYED ) {
                throw new OrderActionException(
                        "Order cannot be completed. Current status: " + order.getOrderStatus(),
                        order.getOrderStatus(), order);
            }

            order.setOrderStatus(OrderStatus.PENDING);
            Order mergedOrder = orderDAO.update(order);
            orderDAO.flush();
            System.out.println("ordersubmiteed id= " + mergedOrder.getId());

            orderSubmittedEvent.fire(new OrderSubmittedEvent(initializeOrder(mergedOrder)));
            return mergedOrder;
        } catch (OptimisticLockException ole) {
            Order currentDbOrder = findOrderByIdAndEnsureInitialized(orderId);
            throw new OrderConflictException(
                    "Could not complete the order as its state changed concurrently. Please refresh.",
                    currentDbOrder);
        }
    }



}
