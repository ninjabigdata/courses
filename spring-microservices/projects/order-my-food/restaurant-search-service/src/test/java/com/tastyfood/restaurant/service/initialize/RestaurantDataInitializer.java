package com.tastyfood.restaurant.service.initialize;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.tastyfood.restaurant.service.constants.Cuisine;
import com.tastyfood.restaurant.service.entity.Address;
import com.tastyfood.restaurant.service.entity.Inventory;
import com.tastyfood.restaurant.service.entity.Item;
import com.tastyfood.restaurant.service.entity.Restaurant;
import com.tastyfood.restaurant.service.repository.InventoryRepository;
import com.tastyfood.restaurant.service.repository.ItemRepository;
import com.tastyfood.restaurant.service.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Profile("test")
public class RestaurantDataInitializer implements CommandLineRunner {

    private final RestaurantRepository restaurantRepository;
    private final ItemRepository itemRepository;
    private final InventoryRepository inventoryRepository;

    @Override
    public void run(String... args) {
        List<String> items = ImmutableList.<String>builder()
                .add("Chilli Chicken")
                .add("Chicken Hakka Noodles")
                .add("Noodles Chicken Soup")
                .add("Indo Chilli Chicken")
                .add("Dragon Chicken")
                .add("Chicken Spring Roll")
                .add("Chilli Fish")
                .build();

        List<Double> prices = ImmutableList.<Double>builder()
                .add(249.0)
                .add(209.0)
                .add(165.0)
                .add(205.0)
                .add(285.0)
                .add(185.0)
                .add(299.0)
                .build();

        Address address = Address.builder()
                .addressFirstLine("51, Main Road")
                .addressSecondLine("GK Nagar")
                .area("Sector 56")
                .city("Chennai")
                .build();

        addRestaurant("Chinese", 350.0, "chinese", address, items, prices, 5.25);

        items = ImmutableList.<String>builder()
                .add("Peri Peri Red Chicken")
                .add("Pepper Chicken Barbeque")
                .add("Chicken Fried Rice")
                .add("Thalappakatti Chicken 65 Briyani")
                .add("Thalappakatti Chicken Briyani")
                .build();

        prices = ImmutableList.<Double>builder()
                .add(329.0)
                .add(309.0)
                .add(229.0)
                .add(309.0)
                .add(289.0)
                .build();

        address = Address.builder()
                .addressFirstLine("51, Main Road")
                .addressSecondLine("GK Nagar")
                .area("Sector 55")
                .city("Chennai")
                .build();

        addRestaurant("Famous Briyani", 300.0, "south indian", address, items, prices, 1.25);
    }

    private void addRestaurant(String name, Double costForTwo, String cuisine,
                               Address address, List<String> itemNames, List<Double> prices,
                               Double distance) {
        Restaurant restaurant = Restaurant.builder()
                .costForTwo(costForTwo)
                .cuisine(Cuisine.getEnum(cuisine))
                .name(name)
                .address(address)
                .distance(distance)
                .build();

        restaurant.setMenu(buildItems(itemNames, prices, restaurant));

        Restaurant savedRestaurant = restaurantRepository.save(restaurant);

        inventoryRepository.saveAll(buildInventory(itemRepository.findAllByRestaurant(savedRestaurant)));
    }

    private Set<Item> buildItems(List<String> itemNames, List<Double> prices, Restaurant restaurant) {
        ImmutableSet.Builder<Item> items = ImmutableSet.builder();

        for (int index = 0; index < itemNames.size(); index++) {
            items.add(Item.builder()
                    .description("Description for ".concat(itemNames.get(index)))
                    .restaurant(restaurant)
                    .name(itemNames.get(index))
                    .price(prices.get(index))
                    .build());
        }

        return items.build();
    }

    private Set<Inventory> buildInventory(Set<Item> items) {
        ImmutableSet.Builder<Inventory> inventories = ImmutableSet.builder();

        items.forEach(item -> inventories.add(
                Inventory.builder()
                    .item(item)
                    .quantityAvailable(0)
                    .build())
        );

        return inventories.build();
    }

}
