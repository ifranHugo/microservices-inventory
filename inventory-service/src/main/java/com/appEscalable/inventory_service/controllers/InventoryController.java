package com.appEscalable.inventory_service.controllers;

import com.appEscalable.inventory_service.services.InventoryServices;
import com.appEscalable.inventory_service.model.dtos.BaseResponse;
import com.appEscalable.inventory_service.model.dtos.OrderItemRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryServices inventoryServices;

    @GetMapping("/{sku}")
    @ResponseStatus(HttpStatus.OK)
    public boolean isInStock(@PathVariable("sku") String sku) {
        return inventoryServices.isInStock(sku);
    }

    @PostMapping("/in-stock")
    @ResponseStatus(HttpStatus.OK)
    public BaseResponse areInStock(@RequestBody List<OrderItemRequest> orderitems) {
        return inventoryServices.areInStock(orderitems);
    }
}
