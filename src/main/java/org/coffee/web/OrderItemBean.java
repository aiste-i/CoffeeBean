package org.coffee.web;

import lombok.Getter;
import lombok.Setter;
import org.coffee.persistence.dao.OrderDAO;
import org.coffee.persistence.dao.OrderItemDAO;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

@Named
@ViewScoped
@Getter
@Setter

public class OrderItemBean implements Serializable{

    @Inject
    private OrderDAO orderDAO;
}
