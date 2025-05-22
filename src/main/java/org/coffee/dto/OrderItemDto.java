package org.coffee.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemDto implements Serializable {

    @NotNull(message = "Product ID cannot be null.")
    private Long productId;

    private String specialRequirements;

    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity = 1;

    private List<Long> addonIngredientIds = new ArrayList<>();
}
