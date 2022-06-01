package com.tastyfood.order.service.service;

import com.tastyfood.order.service.dto.ResponseCode;
import com.tastyfood.order.service.dto.ResponseDTO;
import com.tastyfood.order.service.entity.Order;
import com.tastyfood.order.service.entity.PaymentStatus;
import com.tastyfood.order.service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentService {

    private final OrderRepository orderRepository;

    public ResponseDTO processPayment(Long orderId) {
        ResponseDTO.ResponseDTOBuilder responseBuilder = ResponseDTO.builder();

        try {
            boolean paymentSucceeded = new Random().nextInt(10) <= 8;

            responseBuilder.responseCode(paymentSucceeded ?
                            ResponseCode.SUCCESS : ResponseCode.ERROR)
                    .response(paymentSucceeded);
        } catch (RuntimeException exception) {
            log.error("Exception while processing the payment for orderId - {}", orderId, exception);

            responseBuilder.responseCode(ResponseCode.ERROR)
                    .errorMessage("Payment error");
        }

        log.info("Status of payment processing for orderId - {} is {}", orderId, responseBuilder.build());

        return responseBuilder.build();
    }

    public void processRefund(Long orderId) {
        try {
            Order order = orderRepository.findById(orderId).orElseThrow(
                    () -> new IllegalArgumentException("Invalid orderId"));

            if (PaymentStatus.SUCCESS.equals(order.getPayment().getPaymentStatus())) {
                order.getPayment().setPaymentStatus(PaymentStatus.REFUNDED);

                orderRepository.save(order);

                log.info("Refund initiated for orderId - {}", orderId);
            }

            log.info("Refund cannot be initiated for orderId - {} as payment is not processed",
                    orderId);
        } catch (RuntimeException exception) {
            log.warn("Exception in refund processing", exception);
        }
    }

}
