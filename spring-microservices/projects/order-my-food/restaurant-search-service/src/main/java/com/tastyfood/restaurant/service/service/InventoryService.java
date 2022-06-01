package com.tastyfood.restaurant.service.service;

import com.tastyfood.restaurant.service.constants.ErrorConstants;
import com.tastyfood.restaurant.service.dto.OrderItemDTO;
import com.tastyfood.restaurant.service.dto.ResponseCode;
import com.tastyfood.restaurant.service.dto.ResponseDTO;
import com.tastyfood.restaurant.service.dto.RestaurantOrderDTO;
import com.tastyfood.restaurant.service.entity.Inventory;
import com.tastyfood.restaurant.service.entity.Item;
import com.tastyfood.restaurant.service.repository.InventoryRepository;
import com.tastyfood.restaurant.service.repository.ItemRepository;
import com.tastyfood.restaurant.service.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class InventoryService {

    private final RestaurantRepository restaurantRepository;
    private final InventoryRepository inventoryRepository;
    private final ItemRepository itemRepository;
    private enum InventoryOperation {
        ALLOCATE_ITEMS_FOR_PAYMENT,
        ALLOCATE_ITEMS_FOR_DELIVERY,
        DEALLOCATE_ITEMS_FOR_PAYMENT,
        DEALLOCATE_ITEMS_FOR_DELIVERY
    }

    public ResponseDTO validateOrderItemQuantity(RestaurantOrderDTO restaurantOrderDTO) {
        ResponseDTO.ResponseDTOBuilder responseBuilder = ResponseDTO.builder();
        try {
            String errorMessage = null;
            boolean isValid = false;

            if (restaurantRepository.existsById(restaurantOrderDTO.getRestaurantId())) {
                List<OrderItemDTO> orderedItems = restaurantOrderDTO.getItems();
                List<Long> itemIds = orderedItems.stream().map(OrderItemDTO::getItemId).collect(Collectors.toList());

                List<Inventory> inventoryItems = inventoryRepository.findAllByItemIdInAndItemRestaurantId(
                        itemIds, restaurantOrderDTO.getRestaurantId());

                log.debug("Inventory retrieved is {}", inventoryItems);

                Map<Long, Integer> quantityAvailable = inventoryItems.stream().collect(
                        Collectors.toMap(Inventory::getId, Inventory::getQuantityAvailable));

                log.debug("Inventory quantity is {}", quantityAvailable);

                isValid = orderedItems.stream().allMatch(
                        item -> item.getQuantity() <= quantityAvailable.getOrDefault(item.getItemId(),
                                -1));

                log.info("Status of inventory validation for {} against inventory {} is {}", orderedItems,
                        inventoryItems, isValid);

                if (isValid) {
                    Map<Long, Double> itemPrices = itemRepository.findAllById(itemIds).stream().collect(
                            Collectors.toMap(Item::getId, Item::getPrice));

                    Double calculatedPrice = orderedItems.stream().mapToDouble(
                            item -> item.getQuantity() * itemPrices.getOrDefault(item.getItemId(), 0.0)
                    ).sum();

                    isValid = calculatedPrice.compareTo(restaurantOrderDTO.getPayment().getAmount()) == 0;

                    log.info("Status of payment amount validation for order {} against {} is {}", restaurantOrderDTO,
                            calculatedPrice, isValid);

                    if (isValid) {
                        if (!processInventory(InventoryOperation.ALLOCATE_ITEMS_FOR_PAYMENT, restaurantOrderDTO)) {
                            errorMessage = ErrorConstants.ORDER_ITEM_UNRESERVED;
                        }
                    } else {
                        errorMessage = ErrorConstants.ORDER_AMOUNT_MISMATCH;
                    }
                } else {
                    errorMessage = ErrorConstants.ORDER_ITEMS_UNAVAILABLE;
                }
            } else {
                errorMessage = ErrorConstants.ORDER_NO_RESTAURANT_FOUND;
            }

            if (isValid) {
                responseBuilder.responseCode(ResponseCode.SUCCESS)
                        .response(true);
            } else {
                responseBuilder.responseCode(ResponseCode.ERROR)
                        .errorMessage(errorMessage);
            }
        } catch (RuntimeException exception) {
            log.error("Exception while validating inventory for {}", restaurantOrderDTO, exception);

            responseBuilder.responseCode(ResponseCode.ERROR)
                    .errorMessage(ErrorConstants.ORDER_EXCEPTION);
        }

        log.info("Response from validateOrderItemQuantity for order - {} is {}", restaurantOrderDTO,
                responseBuilder.build());

        return responseBuilder.build();
    }

    public ResponseDTO deallocateQuantityBlockedForPayment(RestaurantOrderDTO restaurantOrderDTO) {
        ResponseDTO.ResponseDTOBuilder responseDTOBuilder = ResponseDTO.builder();

        if (processInventory(InventoryOperation.DEALLOCATE_ITEMS_FOR_PAYMENT, restaurantOrderDTO)) {
            responseDTOBuilder.responseCode(ResponseCode.SUCCESS)
                    .response(true);
        } else {
            responseDTOBuilder.responseCode(ResponseCode.ERROR)
                    .errorMessage(ErrorConstants.INVENTORY_DEALLOCATE_PAID);
        }

        return  responseDTOBuilder.build();
    }

    public ResponseDTO allocateQuantityBlockedForDelivery(RestaurantOrderDTO restaurantOrderDTO) {
        ResponseDTO.ResponseDTOBuilder responseDTOBuilder = ResponseDTO.builder();

        if (processInventory(InventoryOperation.ALLOCATE_ITEMS_FOR_DELIVERY, restaurantOrderDTO)) {
            responseDTOBuilder.responseCode(ResponseCode.SUCCESS)
                    .response(true);
        } else {
            responseDTOBuilder.responseCode(ResponseCode.ERROR)
                    .errorMessage(ErrorConstants.INVENTORY_ALLOCATE_FOR_DELIVERY);
        }

        return  responseDTOBuilder.build();
    }

    public ResponseDTO deallocateQuantityBlockedForDelivery(RestaurantOrderDTO restaurantOrderDTO) {
        ResponseDTO.ResponseDTOBuilder responseDTOBuilder = ResponseDTO.builder();

        if (processInventory(InventoryOperation.DEALLOCATE_ITEMS_FOR_DELIVERY, restaurantOrderDTO)) {
            responseDTOBuilder.responseCode(ResponseCode.SUCCESS)
                    .response(true);
        } else {
            responseDTOBuilder.responseCode(ResponseCode.ERROR)
                    .errorMessage(ErrorConstants.INVENTORY_DEALLOCATE_DELIVERY_ITEMS);
        }

        return  responseDTOBuilder.build();
    }

    private boolean processInventory(InventoryOperation inventoryOperation, RestaurantOrderDTO restaurantOrder) {
        try {
            List<OrderItemDTO> orderedItems = restaurantOrder.getItems();
            List<Long> itemIds = orderedItems.stream().map(OrderItemDTO::getItemId).collect(Collectors.toList());

            List<Inventory> inventoryItems = inventoryRepository.findAllByItemIdInAndItemRestaurantId(
                    itemIds, restaurantOrder.getRestaurantId());

            log.debug("Inventory retrieved is {}", inventoryItems);

            Map<Long, Inventory> inventoryMap = inventoryItems.stream().collect(
                    Collectors.toMap(Inventory::getId, inventory -> inventory));

            switch (inventoryOperation) {
                case ALLOCATE_ITEMS_FOR_PAYMENT:
                    orderedItems.forEach(orderItem -> {
                        Inventory inventory = inventoryMap.get(orderItem.getItemId());

                        inventory.setQuantityBlockedForPayment(inventory.getQuantityBlockedForPayment() + orderItem.getQuantity());
                        inventory.setQuantityAvailable(inventory.getQuantityAvailable() - orderItem.getQuantity());
                    });
                    break;

                case ALLOCATE_ITEMS_FOR_DELIVERY:
                    orderedItems.forEach(orderItem -> {
                        Inventory inventory = inventoryMap.get(orderItem.getItemId());

                        inventory.setQuantityBlockedForPayment(inventory.getQuantityBlockedForPayment() - orderItem.getQuantity());
                        inventory.setQuantityBlockedForDelivery(inventory.getQuantityBlockedForDelivery() + orderItem.getQuantity());
                    });
                    break;

                case DEALLOCATE_ITEMS_FOR_PAYMENT:
                    orderedItems.forEach(orderItem -> {
                        Inventory inventory = inventoryMap.get(orderItem.getItemId());

                        inventory.setQuantityBlockedForPayment(inventory.getQuantityBlockedForPayment() - orderItem.getQuantity());
                        inventory.setQuantityAvailable(inventory.getQuantityAvailable() + orderItem.getQuantity());
                    });
                    break;

                case DEALLOCATE_ITEMS_FOR_DELIVERY:
                    orderedItems.forEach(orderItem -> {
                        Inventory inventory = inventoryMap.get(orderItem.getItemId());

                        inventory.setQuantityBlockedForDelivery(inventory.getQuantityBlockedForDelivery() - orderItem.getQuantity());
                        inventory.setQuantityAvailable(inventory.getQuantityAvailable() + orderItem.getQuantity());
                    });
                    break;
            }

            inventoryRepository.saveAll(inventoryItems);

            log.info("Updated inventory - {}", inventoryItems);

            return true;
        } catch (RuntimeException exception) {
            log.error("Exception while processing inventory for {}", restaurantOrder, exception);

            return false;
        }
    }

}
