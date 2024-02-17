package com.appEscalable.orders_service.services;


import com.appEscalable.orders_service.model.dtos.BaseResponse;
import com.appEscalable.orders_service.model.dtos.OrderItemRequest;
import com.appEscalable.orders_service.model.dtos.OrderRequest;
import com.appEscalable.orders_service.model.dtos.OrderResponse;
import com.appEscalable.orders_service.model.entities.Order;
import com.appEscalable.orders_service.model.entities.OrderItems;
import com.appEscalable.orders_service.repositories.OrderRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServise {

    private final OrderRepository orderRepository;
    @Autowired
    private final RestTemplate restTemplate;

    private final WebClient.Builder webClientBuilder;


    public void placeOrder(OrderRequest orderRequest) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<List<OrderItemRequest>> requestEntity = new HttpEntity<>(orderRequest.getOrderItems(), headers);


        ResponseEntity<BaseResponse> responseEntity = restTemplate.exchange(
                "http://inventory-service/api/inventory/in-stock",
                HttpMethod.POST,
                requestEntity,
                BaseResponse.class);
        BaseResponse result = responseEntity.getBody();


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
        // OrderItems @Builder
        return OrderItems.builder()
                .id(orderItemRequest.getId())
                .sku(orderItemRequest.getSku())
                .price(orderItemRequest.getPrice())
                .quantity(orderItemRequest.getQuantity())
                .order(order)
                .build();
    }
}
