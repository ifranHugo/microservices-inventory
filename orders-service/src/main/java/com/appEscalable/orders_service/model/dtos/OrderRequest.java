package com.appEscalable.orders_service.model.dtos;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class OrderRequest {
    private List<OrderItemRequest> orderItems;
}
