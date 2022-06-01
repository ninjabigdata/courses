package com.tastyfood.order.service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tastyfood.order.service.dto.CustomerOrderDTO;
import com.tastyfood.order.service.dto.ResponseCode;
import com.tastyfood.order.service.dto.ResponseDTO;
import com.tastyfood.order.service.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @InjectMocks
    private OrderController controller;
    @Mock
    private OrderService service;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    private static final String SUCCESS = "success";
    private static final Long ORDER_ID = 1L;

    @BeforeEach
    void initializeMockMvc() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void createOrder() throws Exception {
        CustomerOrderDTO order = CustomerOrderDTO.builder().build();
        doReturn(SUCCESS).when(service).processOrder(order);

        mockMvc.perform(post("/order/").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(order)))
                .andExpect(status().isOk())
                .andExpect(content().string(SUCCESS));

        verify(service).processOrder(order);
    }

    @Test
    void updateOrder() throws Exception {
        String mobileNumber = "1111122333";

        doReturn(SUCCESS).when(service).updateOrder(ORDER_ID, mobileNumber);

        mockMvc.perform(put("/order/".concat(ORDER_ID.toString()).concat("/").concat(mobileNumber)))
                .andExpect(status().isOk())
                .andExpect(content().string(SUCCESS));

        verify(service).updateOrder(ORDER_ID, mobileNumber);
    }

    @Test
    void viewOrder() throws Exception {
        ResponseDTO response = ResponseDTO.builder()
                .responseCode(ResponseCode.SUCCESS)
                .build();

        doReturn(response).when(service).viewOrder(ORDER_ID);

        mockMvc.perform(get("/order/".concat(ORDER_ID.toString())))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(response)));

        verify(service).viewOrder(ORDER_ID);
    }

    @Test
    void cancelOrder() throws Exception {
        doReturn(SUCCESS).when(service).cancelOrder(ORDER_ID);

        mockMvc.perform(get("/order/".concat(ORDER_ID.toString()).concat("/cancel")))
                .andExpect(status().isOk())
                .andExpect(content().string(SUCCESS));

        verify(service).cancelOrder(ORDER_ID);
    }
}