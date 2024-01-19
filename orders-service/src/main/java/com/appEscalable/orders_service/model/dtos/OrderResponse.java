package com.appEscalable.orders_service.model.dtos;

import java.util.List;

public record OrderResponse (Long id , String orderNumber, List<OrderItemRequest> orderItems) {
}
