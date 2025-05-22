package org.coffee.web;


import lombok.Getter;
import lombok.Setter;
import org.coffee.persistence.entity.Order;
import org.coffee.rest.OrderApiClient;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

@Named
@ViewScoped
public class OrderConfirmationBean implements Serializable {

    @Inject
    private OrderApiClient orderApiClient;

    @Setter
    @Getter
    private Long orderId;
    @Getter
    private Order order;

    public void loadOrder() {
        if (orderId != null) {
            try {
                this.order = orderApiClient.getOrderById(orderId);
            } catch (Exception e) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Order error", "Order could not be loaded. Please try again,"));
            }
        }
    }

}