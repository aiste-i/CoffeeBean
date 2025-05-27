package org.coffee.web;

import lombok.Getter;
import lombok.Setter;
import org.coffee.dto.OrderCreationDto;
import org.coffee.dto.OrderItemDto;
import org.coffee.persistence.entity.Ingredient;
import org.coffee.persistence.entity.Order;
import org.coffee.rest.OrderApiClient;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

@Named
@ViewScoped
public class CheckoutConfirmBean implements Serializable {

    @Inject
    private OrderBean orderBean;

    @Inject
    private OrderApiClient orderApiClient;

    @Setter
    @Getter
    private String orderNameInput;

    @Setter
    @Getter
    private String customerNameInput;

    @Setter
    @Getter
    private String customerEmailInput;

    @Getter
    private Order orderToConfirm;

    @Setter
    @Getter
    private boolean stripeReady = false;

    @Getter
    @Setter
    private Long orderId;

    @PostConstruct
    public void init() {
        this.orderToConfirm = orderBean.getCurrentOrder();
        if (this.orderToConfirm == null || orderBean.isOrderEmpty()) {
            try {
                FacesContext.getCurrentInstance().getExternalContext().redirect("order-view.xhtml");
            } catch (Exception e) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Order error", "Cannot initialize order."));
            }
            return;
        }
        this.orderNameInput = this.orderToConfirm.getName() != null ? this.orderToConfirm.getName() : "My Coffee Order";
        this.customerNameInput = this.orderToConfirm.getCustomerName();
        this.customerEmailInput = this.orderToConfirm.getCustomerEmail();
    }

    @Transactional
    public void submitFinalOrder() {
        if (orderBean.isOrderEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Order is Empty", "Cannot submit an empty order."));
            return;
        }

        Order orderFromSession = orderBean.getCurrentOrder();

        OrderCreationDto creationDto = new OrderCreationDto();
        creationDto.setOrderName(this.orderNameInput);
        creationDto.setCustomerName(this.customerNameInput);
        creationDto.setCustomerEmail(this.customerEmailInput);
        creationDto.setCustomerId(orderFromSession.getUser().getId());

        creationDto.setItems(
                orderFromSession.getItems().stream().map(oi -> {
                    OrderItemDto itemDto = new OrderItemDto();
                    itemDto.setProductId(oi.getProduct().getId());
                    itemDto.setSpecialRequirements(oi.getSpecialRequirements());
                    itemDto.setQuantity(oi.getQuantity());
                    if (oi.getAddons() != null) {
                        itemDto.setAddonIngredientIds(
                                oi.getAddons().stream().map(Ingredient::getId).collect(Collectors.toList())
                        );
                    }
                    return itemDto;
                }).collect(Collectors.toList())
        );

        try {
            Order persistedOrder = orderApiClient.createOrder(creationDto);
            this.stripeReady = true;
            this.orderId = persistedOrder.getId();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Order Submitted Successfully!",
                            "Your Order ID is: " + persistedOrder.getId()));

        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_FATAL, "Order Submission Failed",
                            "Could not submit your order. Please try again. Error: " + e.getMessage()));
        }
    }
}