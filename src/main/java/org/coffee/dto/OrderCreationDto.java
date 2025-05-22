package org.coffee.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class OrderCreationDto implements Serializable {

    @NotNull(message = "Order name cannot be null")
    @Size(min = 1, max = 255, message = "Order name must be between 1 and 255 characters")
    private String orderName;

    @Size(max = 100, message = "Customer name cannot exceed 100 characters")
    private String customerName;

    @Email(message = "Invalid email format for customer email")
    @Size(max = 255, message = "Customer email cannot exceed 255 characters")
    private String customerEmail;

    @NotEmpty(message = "Order must contain at least one item")
    @Valid
    private List<OrderItemDto> items;
}

