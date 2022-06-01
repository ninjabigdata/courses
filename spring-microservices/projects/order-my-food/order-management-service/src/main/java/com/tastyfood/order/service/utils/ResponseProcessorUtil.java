package com.tastyfood.order.service.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tastyfood.order.service.dto.ResponseCode;
import com.tastyfood.order.service.dto.ResponseDTO;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.jms.Message;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class ResponseProcessorUtil {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static boolean isSuccess(Message response) {
        boolean isSuccess;

        if (Objects.isNull(response)) {
            isSuccess = false;
        } else {
            try {
                ResponseDTO responseDTO = OBJECT_MAPPER.readValue(response.getBody(String.class), ResponseDTO.class);

                isSuccess = responseDTO.getResponseCode().equals(ResponseCode.SUCCESS)
                        && Objects.nonNull(responseDTO.getResponse());
            } catch (Exception e) {
                log.error("Exception while processing the JMS response - {}", response, e);

                isSuccess = false;
            }
        }
        return isSuccess;
    }

    public static String getErrorMessage(Message response) {
        String errorMessage = "Internal error. Please contact support";

        if (Objects.nonNull(response)) {
            try {
                ResponseDTO responseDTO = OBJECT_MAPPER.readValue(response.getBody(String.class), ResponseDTO.class);

                errorMessage = responseDTO.getErrorMessage();
            } catch (Exception e) {
                log.error("Exception while processing the JMS response - {}", response, e);
            }
        }

        return errorMessage;
    }

}
