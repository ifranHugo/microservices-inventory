package com.appEscalable.inventory_service.services;

import com.appEscalable.inventory_service.model.entities.Inventory;
import com.appEscalable.inventory_service.repositories.InventoryRepository;
import com.appEscalable.inventory_service.model.dtos.BaseResponse;
import com.appEscalable.inventory_service.model.dtos.OrderItemRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryServices {

    private final InventoryRepository inventoryRepository;

    public BaseResponse areInStock(List<OrderItemRequest> orderItems) {
        var errorList= new ArrayList<String>();

        List<String> skus = orderItems.stream().map(OrderItemRequest::getSku).toList();
        List<Inventory> inventoryList = inventoryRepository.findBySkuIn(skus);

        orderItems.forEach(orderItem->{
            var inventory = inventoryList.stream().filter(value -> value.getSku().equals(orderItem.getSku())).findFirst();
            if(inventory.isEmpty()){
               errorList.add("Product with sku "+ orderItem.getSku() + "doeas not exist");
                //Esta implementacion de este metodo no es la via mas eficiente pero para efectos educativos esta bien
            }else if(inventory.get().getQuantity() < orderItem.getQuantity()){
                errorList.add("Product with sku "+ orderItem.getSku() + "with insufficient quantity");
            }
        });
        return errorList.size()>0 ? new BaseResponse(errorList.toArray(new String[0])) : new BaseResponse(null);
    }

    public boolean isInStock(String sku) {
        var inventory = inventoryRepository.findBySku(sku);
        return inventory.filter(value -> value.getQuantity() > 0).isPresent();
    }
}
