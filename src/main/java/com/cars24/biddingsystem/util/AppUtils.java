package com.cars24.biddingsystem.util;

import com.cars24.biddingsystem.dto.APIResponse;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;

import java.util.UUID;

public class AppUtils {
    public static APIResponse populateAPIResponse(ResponseMessage mapping, HttpStatus httpStatus, Object response) {
        APIResponse apiResponse = new APIResponse();
        return apiResponse.setMessage(mapping.getMessage())
                .setCode(mapping.getCode())
                .setResponse(response).setStatus(httpStatus);
    }

    public static APIResponse populateAPIResponse(ResponseMessage mapping, HttpStatus httpStatus) {
        APIResponse apiResponse = new APIResponse();
        return apiResponse.setMessage(mapping.getMessage())
                .setCode(mapping.getCode()).setStatus(httpStatus);
    }

    public static void setRequestId(String requestId) {
       if(StringUtils.isEmpty(requestId)) {
           MDC.put(Constants.REQUEST_ID, generateRequestId());
       }
       else {
           MDC.put(Constants.REQUEST_ID, requestId);
       }
    }

    public static String generateRequestId() {
        return UUID.randomUUID().toString();
    }

    public static String getRequestId() {
        return MDC.get(Constants.REQUEST_ID);
    }
}
