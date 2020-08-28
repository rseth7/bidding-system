package com.cars24.biddingsystem.dto;

import com.cars24.biddingsystem.util.ResponseMessage;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Accessors(chain = true)
@ToString
@Setter
@Getter
@NoArgsConstructor
public class APIResponse implements Serializable {
    private Object response;
    private String code;
    private String message;
    private HttpStatus status;

    public APIResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public APIResponse(ResponseMessage mapping) {
        this.code = mapping.getCode();
        this.message = mapping.getMessage();
    }

    public APIResponse(HttpStatus status, String code, String message, Object response) {
        this(code, message);
        this.status = status;
        this.response = response;
    }

    public APIResponse(HttpStatus status, ResponseMessage mapping, Object response) {
        this(mapping);
        this.status = status;
        this.response = response;
    }
}
