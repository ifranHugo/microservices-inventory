package com.appEscalable.orders_service.services;


import com.appEscalable.orders_service.model.dtos.BaseResponse;
import com.appEscalable.orders_service.model.dtos.OrderItemRequest;
import com.appEscalable.orders_service.model.dtos.OrderRequest;
import com.appEscalable.orders_service.model.dtos.OrderResponse;
import com.appEscalable.orders_service.model.entities.Order;
import com.appEscalable.orders_service.model.entities.OrderItems;
import com.appEscalable.orders_service.repositories.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServise {

    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;
    public void placeOrder(OrderRequest orderRequest) {
        BaseResponse result= this.webClientBuilder.build()
                .post()
                .uri("http://localhost:8083/api/inventory/in-stock")
                .bodyValue(orderRequest.getOrderItems())
                .retrieve()
                .bodyToMono(BaseResponse.class)
                .block();
        if(result!=null &&!result.hasError()) {
            Order order = new Order();
            // Genera un número de pedido único utilizando UUID
            order.setOrderNumber(UUID.randomUUID().toString());
            // Mapea los OrderItemRequest a OrderItem y los asigna a Order
            order.setOrderItems(orderRequest.getOrderItems().stream().
                    map(orderItemRequest -> mapOrderitemRequestToOrderItem(orderItemRequest, order))
                    .toList());
            this.orderRepository.save(order);
        }else{
            throw new IllegalArgumentException("Error al procesar el pedido");
        }
    }


    public List<OrderResponse> getAllOrders(){
        List<Order> orders = this.orderRepository.findAll();
        return orders.stream().map(this::mapToOrderResponse).toList();
    }

    private OrderResponse mapToOrderResponse(Order order) {
        return new OrderResponse(order.getId(),order.getOrderNumber(), order.getOrderItems().stream().map(this::mapToOrderItemRequest).toList());
    }

    private OrderItemRequest mapToOrderItemRequest(OrderItems orderItems) {
        return new OrderItemRequest(orderItems.getId(),orderItems.getSku(),orderItems.getPrice(),orderItems.getQuantity());
    }

    // toma un objeto OrderItemRequest y un objeto Order, utiliza la información del
    // primero para construir un objeto OrderItem asociado al segundo, y devuelve el OrderItem resultante.
    private OrderItems mapOrderitemRequestToOrderItem(OrderItemRequest orderItemRequest, Order order) {
        return OrderItems.builder()
                .id(orderItemRequest.getId())
                .sku(orderItemRequest.getSku())
                .price(orderItemRequest.getPrice())
                .quantity(orderItemRequest.getQuantity())
                .order(order)
                .build();
    }
}
