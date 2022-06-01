package com.tastyfood.order.service.client;

import com.tastyfood.order.service.dto.RestaurantOrderDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RestaurantFeignClientTest {

    @Test
    void inventoryFallback() {
        assertEquals("error", new RestaurantFeignClient() {
            @Override
            public String validateOrder(RestaurantOrderDTO order) {
                return null;
            }

            @Override
            public String deallocatePaymentInventory(RestaurantOrderDTO order) {
                return null;
            }

            @Override
            public String allocateDeliveryInventory(RestaurantOrderDTO order) {
                return null;
            }

            @Override
            public String deallocateDeliveryInventory(RestaurantOrderDTO order) {
                return null;
            }
        }.inventoryFallback(new RestaurantOrderDTO(), new RuntimeException()));
    }
}