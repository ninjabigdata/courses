package com.tastyfood.restaurant.service.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tastyfood.restaurant.service.constants.ErrorConstants;
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
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class InventoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private static ObjectMapper objectMapper;
    private static RestaurantOrderDTO validOrderDTO;
    private static RestaurantOrderDTO invalidRestaurantOrderDTO;
    private static RestaurantOrderDTO invalidPaymentOrderDTO;
    private static RestaurantOrderDTO invalidQuantityOrderDTO;
    private static RestaurantOrderDTO invalidOrderDTO;
    private static String successResponse;

    @BeforeAll
    static void initialize() throws JsonProcessingException {
        objectMapper = new ObjectMapper();

        successResponse = objectMapper.writeValueAsString(
                ResponseDTO.builder()
                        .responseCode(ResponseCode.SUCCESS)
                        .response(true)
                        .build()
        );

        PaymentDTO paymentDTO = PaymentDTO.builder()
                .amount(209.0)
                .build();

        List<OrderItemDTO> items = Collections.singletonList(
                OrderItemDTO.builder()
                        .itemId(2L)
                        .quantity(1)
                        .build()
        );

        validOrderDTO = RestaurantOrderDTO.builder()
                .restaurantId(1L)
                .payment(paymentDTO)
                .items(items)
                .build();

        invalidRestaurantOrderDTO = RestaurantOrderDTO.builder()
                .restaurantId(100L)
                .payment(paymentDTO)
                .items(items)
                .build();

        invalidPaymentOrderDTO = RestaurantOrderDTO.builder()
                .restaurantId(1L)
                .payment(PaymentDTO.builder()
                        .amount(409.0)
                        .build())
                .items(items)
                .build();

        invalidQuantityOrderDTO = RestaurantOrderDTO.builder()
                .restaurantId(1L)
                .payment(paymentDTO)
                .items(Collections.singletonList(
                        OrderItemDTO.builder()
                                .itemId(109L)
                                .quantity(1)
                                .build()
                ))
                .build();

        invalidOrderDTO = new RestaurantOrderDTO();
    }

    @Test
    @Transactional
    void validate_ValidOrder() throws Exception {


        mockMvc.perform(post("/inventory/validate").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validOrderDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string(successResponse));
    }

    @Test
    @Transactional
    void validate_InvalidPaymentOrder() throws Exception {
        String response = constructErrorResponse(ErrorConstants.ORDER_AMOUNT_MISMATCH);

        mockMvc.perform(post("/inventory/validate").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidPaymentOrderDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string(response));
    }

    @Test
    @Transactional
    void validate_InvalidItemsOrder() throws Exception {
        String response = constructErrorResponse(ErrorConstants.ORDER_ITEMS_UNAVAILABLE);

        mockMvc.perform(post("/inventory/validate").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidQuantityOrderDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string(response));
    }

    @Test
    @Transactional
    void validate_InvalidRestaurantOrder() throws Exception {
        String response = constructErrorResponse(ErrorConstants.ORDER_NO_RESTAURANT_FOUND);

        mockMvc.perform(post("/inventory/validate").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRestaurantOrderDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string(response));
    }

    @Test
    void deallocateQuantityBlockedForPayment_success() throws Exception {
        mockMvc.perform(post("/inventory/deallocate-payment-order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validOrderDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string(successResponse));
    }

    @Test
    void deallocateQuantityBlockedForPayment_exception() throws Exception {
        String response = constructErrorResponse(ErrorConstants.INVENTORY_DEALLOCATE_PAID);

        mockMvc.perform(post("/inventory/deallocate-payment-order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidOrderDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string(response));
    }

    @Test
    void allocateQuantityBlockedForDelivery() throws Exception  {
        mockMvc.perform(post("/inventory/allocate-delivery-order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validOrderDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string(successResponse));
    }

    @Test
    void allocateQuantityBlockedForDelivery_exception() throws Exception {
        String response = constructErrorResponse(ErrorConstants.INVENTORY_ALLOCATE_FOR_DELIVERY);

        mockMvc.perform(post("/inventory/allocate-delivery-order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidOrderDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string(response));
    }

    @Test
    void deallocateQuantityBlockedForDelivery_success() throws Exception  {
        mockMvc.perform(post("/inventory/deallocate-delivery-order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validOrderDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string(successResponse));
    }

    @Test
    void deallocateQuantityBlockedForDelivery_exception() throws Exception  {
        String response = constructErrorResponse(ErrorConstants.INVENTORY_DEALLOCATE_DELIVERY_ITEMS);

        mockMvc.perform(post("/inventory/deallocate-delivery-order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidOrderDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string(response));
    }

    private String constructErrorResponse(String errorMessage) throws JsonProcessingException {
        return objectMapper.writeValueAsString(
                ResponseDTO.builder()
                        .responseCode(ResponseCode.ERROR)
                        .errorMessage(errorMessage)
                        .build());
    }

}