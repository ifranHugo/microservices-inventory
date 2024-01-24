package com.appEscalable.inventory_service.model.dtos;


import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemRequest {
    private Long id;
    private String sku;
    private Double price;



    private Long quantity;


}
