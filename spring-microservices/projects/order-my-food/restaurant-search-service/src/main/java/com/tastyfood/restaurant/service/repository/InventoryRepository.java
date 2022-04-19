package com.tastyfood.restaurant.service.repository;

import com.tastyfood.restaurant.service.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
}
