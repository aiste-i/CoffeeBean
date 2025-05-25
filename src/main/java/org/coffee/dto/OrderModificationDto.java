package org.coffee.dto;

import lombok.AllArgsConstructor;
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
@AllArgsConstructor
public class OrderModificationDto implements Serializable {

    @NotEmpty(message = "Updated items list cannot be empty")
    @Valid
    private List<OrderItemDto> updatedItems;

    @NotNull
    private Long customerId;

    @Size(max = 100, message = "Customer name cannot exceed 100 characters")
    private String customerName;

    @Email(message = "Invalid customer email format")
    @Size(max = 255, message = "Customer email cannot exceed 255 characters")
    private String customerEmail;

    @NotNull(message = "Order version must be provided for modification")
    private Integer version;
}