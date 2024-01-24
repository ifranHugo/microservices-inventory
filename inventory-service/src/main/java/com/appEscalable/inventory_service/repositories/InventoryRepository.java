package com.appEscalable.inventory_service.repositories;

import com.appEscalable.inventory_service.model.entities.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    //SELECT i FROM Inventory i WHERE i.sku = :sku
    Optional<Inventory> findBySku(String sku);

    List<Inventory> findBySkuIn(List<String> sku);
}
