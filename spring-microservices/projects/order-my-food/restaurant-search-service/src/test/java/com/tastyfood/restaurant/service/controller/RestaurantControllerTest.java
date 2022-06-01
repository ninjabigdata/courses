package com.tastyfood.restaurant.service.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.tastyfood.restaurant.service.constants.Cuisine;
import com.tastyfood.restaurant.service.dto.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class RestaurantControllerTest {

    @Autowired
    private MockMvc mockMvc;
    private static ObjectMapper objectMapper;
    private static String restaurantSearchSuccessResponse;
    private static String restaurantSearchErrorResponse;
    private static String menuSuccessResponse;
    private static String menuErrorResponse;

    @BeforeAll
    static void initialize() throws JsonProcessingException {
        objectMapper = new ObjectMapper();

        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setAddressFirstLine("51, Main Road");
        addressDTO.setAddressSecondLine("GK Nagar");
        addressDTO.setArea("Sector 55");
        addressDTO.setCity("Chennai");
        RestaurantDTO restaurantDTO = new RestaurantDTO();
        restaurantDTO.setId(2L);
        restaurantDTO.setName("Famous Briyani");
        restaurantDTO.setCostForTwo(300.0);
        restaurantDTO.setCuisine(Cuisine.SOUTH_INDIAN);
        restaurantDTO.setAddress(addressDTO);
        restaurantDTO.setDistance(1.25);

        restaurantSearchSuccessResponse = objectMapper.writeValueAsString(ResponseDTO.builder()
                .responseCode(ResponseCode.SUCCESS)
                .response(Collections.singletonList(restaurantDTO))
                .build());

        restaurantSearchErrorResponse = objectMapper.writeValueAsString(ResponseDTO.builder()
                .responseCode(ResponseCode.ERROR)
                .response("Atleast one criterion is required for searching")
                .build());

        ImmutableList<ItemDTO> items = ImmutableList.<ItemDTO>builder()
                .add(new ItemDTO(8L, "Peri Peri Red Chicken", "Description for Peri Peri Red Chicken", 329.0))
                .add(new ItemDTO(9L, "Pepper Chicken Barbeque", "Description for Pepper Chicken Barbeque", 309.0))
                .add(new ItemDTO(10L, "Chicken Fried Rice", "Description for Chicken Fried Rice", 229.0))
                .add(new ItemDTO(11L, "Thalappakatti Chicken 65 Briyani", "Description for Thalappakatti Chicken 65 Briyani", 309.0))
                .add(new ItemDTO(12L, "Thalappakatti Chicken Briyani", "Description for Thalappakatti Chicken Briyani", 289.0))
                .build();

        menuSuccessResponse = objectMapper.writeValueAsString(ResponseDTO.builder()
                .responseCode(ResponseCode.SUCCESS)
                .response(items)
                .build());

        menuErrorResponse = objectMapper.writeValueAsString(ResponseDTO.builder()
                .responseCode(ResponseCode.ERROR)
                .response("Restaurant Id is required")
                .build());
    }

    @Test
    @Transactional
    void searchWithAllCriteria() throws Exception {
        RestaurantSearchDTO restaurantSearchDTO = RestaurantSearchDTO.builder()
                .name("famous")
                .distance(5.0)
                .cuisine(Cuisine.SOUTH_INDIAN)
                .budget(350)
                .location("sector")
                .build();

        mockMvc.perform(post("/search").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(restaurantSearchDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string(restaurantSearchSuccessResponse));
    }

    @Test
    @Transactional
    void searchByRestaurantName() throws Exception {
        RestaurantSearchDTO restaurantSearchDTO = RestaurantSearchDTO.builder()
                .name("famous")
                .build();

        mockMvc.perform(post("/search").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(restaurantSearchDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string(restaurantSearchSuccessResponse));
    }

    @Test
    @Transactional
    void searchByDistance() throws Exception {
        RestaurantSearchDTO restaurantSearchDTO = RestaurantSearchDTO.builder()
                .distance(5.0)
                .build();

        mockMvc.perform(post("/search").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(restaurantSearchDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string(restaurantSearchSuccessResponse));
    }

    @Test
    @Transactional
    void searchByCuisine() throws Exception {
        RestaurantSearchDTO restaurantSearchDTO = RestaurantSearchDTO.builder()
                .cuisine(Cuisine.SOUTH_INDIAN)
                .build();

        mockMvc.perform(post("/search").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(restaurantSearchDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string(restaurantSearchSuccessResponse));
    }

    @Test
    @Transactional
    void searchByBudget() throws Exception {
        RestaurantSearchDTO restaurantSearchDTO = RestaurantSearchDTO.builder()
                .budget(300)
                .build();

        mockMvc.perform(post("/search").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(restaurantSearchDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string(restaurantSearchSuccessResponse));
    }

    @Test
    @Transactional
    void searchByLocation() throws Exception {
        RestaurantSearchDTO restaurantSearchDTO = RestaurantSearchDTO.builder()
                .location("sector 55")
                .build();

        mockMvc.perform(post("/search").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(restaurantSearchDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string(restaurantSearchSuccessResponse));
    }

    @Test
    void searchWithNoCriteria() throws Exception {
        RestaurantSearchDTO restaurantSearchDTO = RestaurantSearchDTO.builder()
                .build();

        mockMvc.perform(post("/search").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(restaurantSearchDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string(restaurantSearchErrorResponse));
    }

    @Test
    void getMenu() throws Exception {
        mockMvc.perform(get("/2/menu"))
                .andExpect(status().isOk())
                .andExpect(content().string(menuSuccessResponse));
    }

    @Test
    void getMenuError() throws Exception {
        mockMvc.perform(get("/menu"))
                .andExpect(status().isOk())
                .andExpect(content().string(menuErrorResponse));
    }
}