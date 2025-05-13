package org.coffee.web;

import lombok.Getter;
import lombok.Setter;
import org.coffee.persistence.dao.OrderDAO;
import org.coffee.persistence.dao.UserDAO;
import org.coffee.persistence.entity.Order;
import org.coffee.persistence.entity.OrderItem;
import org.coffee.persistence.entity.User;
import org.coffee.persistence.entity.enums.OrderStatus;
import org.coffee.service.CredentialService;

import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Objects;

@Named
@ViewScoped
@Getter
@Setter
public class OrderBean implements Serializable{

    @Inject
    private OrderDAO orderDAO;

    @Inject
    private UserDAO userDAO;

    @Inject
    private CredentialService credentialService;

    @Getter
    @Setter
    private Order currentOrder;

    public OrderBean() {
        initializeNewOrder();
    }

    private void initializeNewOrder() {
        this.currentOrder = new Order();
        // Set a temporary name or leave it null until formal submission
        this.currentOrder.setName("Temporary Cart Order");
        this.currentOrder.setTotalPrice(BigDecimal.ZERO);
        this.currentOrder.setOrderStatus(OrderStatus.PENDING);
        this.currentOrder.setItems(new ArrayList<>());

        try{
            User currentUser = userDAO.findByUsername(credentialService.getCurrentUsername());
            this.currentOrder.setUser(currentUser);
        }catch (NullPointerException ignored){
            this.currentOrder.setUser(null);
        }
    }

    public Order getCurrentOrder() {
        if (currentOrder == null) {
            initializeNewOrder();
        }
        return currentOrder;
    }

    public void clearOrder() {
        initializeNewOrder();
        System.out.println("Current order cleared.");
    }

    public void recalculateTotalPrice() {
        if (currentOrder != null && currentOrder.getItems() != null) {
            currentOrder.setTotalPrice(currentOrder.calculateTotalPrice());
        }
    }

    public void removeItemFromOrder(OrderItem itemToRemove) {
        if (itemToRemove != null && currentOrder.getItems() != null) {
            currentOrder.getItems().removeIf(item -> Objects.equals(item.getId(), itemToRemove.getId()) || item.equals(itemToRemove)); // Handle transient vs persisted
            currentOrder.removeItem(itemToRemove); // Ensure bidirectional link is broken
            recalculateTotalPrice();
        }
    }

    public boolean isOrderEmpty() {
        return currentOrder == null || currentOrder.getItems() == null || currentOrder.getItems().isEmpty();
    }

    public String proceedToFinalSubmission() {
        if (isOrderEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new javax.faces.application.FacesMessage(javax.faces.application.FacesMessage.SEVERITY_WARN,
                            "Empty Order", "Your order is empty."));
            return null; // Stay on the same page
        }
        // In a real app, you'd now pass this 'currentOrder' object to an OrderService
        // to be formally saved, payment processed (later), and status updated.
        // For now, let's assume this action signifies it's ready for that next step.

        // You might want to put the 'currentOrder' into Flash scope
        // if the next page (actual submission/confirmation) needs it.
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("finalOrderToProcess", currentOrder);

        // For now, let's just simulate a redirect to a placeholder "confirmation"
        // or the actual page that handles persistence.
        // The actual persistence and redirect to a *final* confirmation page
        // would happen in a bean similar to 'OrderReviewBean' from the previous example.
        // This 'CurrentOrderBean' is more like the 'ShoppingCartBean'.

        // Let's redirect to a hypothetical page that uses OrderReviewBean for final processing.
        // To fulfill the user story, you need another bean (like the OrderReviewBean I showed before)
        // that takes this 'currentOrder' (perhaps from Flash scope), calls an OrderService to persist it,
        // and then handles success/failure redirection and cart clearing.

        // For now, we just navigate, assuming the user story's "Order Review Screen"
        // is THIS page, and "submit" means "go to the actual processing step".
        return "orderFinalize.xhtml?faces-redirect=true"; // A new page for final submission logic
    }
}
