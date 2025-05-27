package org.coffee.web;

import org.coffee.persistence.entity.Ingredient;
import org.coffee.persistence.entity.Order;
import org.coffee.persistence.entity.OrderItem;
import org.coffee.persistence.entity.Product;
import org.coffee.persistence.entity.User;
import org.coffee.persistence.entity.enums.OrderStatus;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Named
@SessionScoped
public class OrderBean implements Serializable {

    private Order currentOrder;

    @Inject
    private UserSessionBean userSessionBean;

    @PostConstruct
    public void init() {
        createNewOrder();
    }

    private void createNewOrder() {
        currentOrder = new Order();
        currentOrder.setOrderStatus(OrderStatus.PENDING);
        currentOrder.setTotalPrice(BigDecimal.ZERO);

        if (userSessionBean != null && userSessionBean.isLoggedIn()) {
            User loggedUser = userSessionBean.getLoggedInUserEntity();
            if (loggedUser != null) {
                currentOrder.setUser(loggedUser);
                currentOrder.setCustomerEmail(loggedUser.getEmail());
            }
        }
    }

    public Order getCurrentOrder() {
        if (currentOrder == null) {
            init();
        }
        return currentOrder;
    }

    public void addItemToOrder(Product product, int quantity) {
        addItemToOrder(product, quantity, null);
    }

    public void addItemToOrder(Product product, int quantity, List<Ingredient> addons) {
        if (product == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "No product selected.", ""));
            return;
        }
        int quantityToAdd = quantity > 0 ? quantity : 1;

        Optional<OrderItem> existingItemOpt = currentOrder.getItems().stream()
                .filter(oi -> oi.getProduct().getId().equals(product.getId()) &&
                        ((addons == null || addons.isEmpty()) && (oi.getAddons() == null || oi.getAddons().isEmpty()) ||
                         (addons != null && !addons.isEmpty() && oi.getAddons() != null && !oi.getAddons().isEmpty() &&
                          addons.size() == oi.getAddons().size() && oi.getAddons().containsAll(addons))) &&
                        (oi.getSpecialRequirements() == null || oi.getSpecialRequirements().isEmpty()))
                .findFirst();

        if (existingItemOpt.isPresent()) {
            OrderItem existingItem = existingItemOpt.get();
            existingItem.setQuantity(existingItem.getQuantity() + quantityToAdd);
        } else {
            OrderItem newItem = new OrderItem();
            newItem.setProduct(product);
            newItem.setName(product.getName());
            newItem.setQuantity(quantityToAdd);

            if (addons != null && !addons.isEmpty()) {
                newItem.getAddons().addAll(addons);
            }

            currentOrder.addItem(newItem);
        }
        currentOrder.setTotalPrice(currentOrder.calculateTotalPrice());

        String addonInfo = "";
        if (addons != null && !addons.isEmpty()) {
            addonInfo = " with " + addons.size() + " add-on(s)";
        }

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Item Added", product.getName() + addonInfo + " added to your order."));
    }

    public void removeItemFromOrder(OrderItem itemToRemove) {
        if (currentOrder != null && itemToRemove != null) {
            boolean removed = currentOrder.getItems().removeIf(item -> item.equals(itemToRemove) || (item.getProduct().getId().equals(itemToRemove.getProduct().getId()) && item.getQuantity().equals(itemToRemove.getQuantity())));

            if(removed) {
                currentOrder.calculateTotalPrice();
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Item Removed", ""));
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_WARN, "Item not found", "Could not remove item."));
            }
        }
    }

    public void clearOrder() {
        createNewOrder();
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Order Cleared", "Your order has been emptied."));
    }

    public boolean isOrderEmpty() {
        return currentOrder == null || currentOrder.getItems() == null || currentOrder.getItems().isEmpty();
    }

    public String proceedToFinalSubmission() {
        if (isOrderEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Cannot Proceed", "Your order is empty."));
            return null;
        }

        return "/user/checkout-confirm.xhtml?faces-redirect=true";
    }

    public void updateOrderTotal() {
        if (currentOrder != null) {
            currentOrder.setTotalPrice(currentOrder.calculateTotalPrice());
        }
    }
}
