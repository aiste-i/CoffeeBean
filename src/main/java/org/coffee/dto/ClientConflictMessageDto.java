package org.coffee.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.coffee.persistence.entity.Order;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClientConflictMessageDto implements Serializable {
    public String conflictType;
    public String message;
    public Order latestOrderState;
}
