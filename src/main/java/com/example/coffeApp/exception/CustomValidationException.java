package com.example.coffeApp.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Map;

@Data
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CustomValidationException extends RuntimeException {

    private Map<String, Object> data;

    public CustomValidationException(String message) {
        super(message);
    }

    public CustomValidationException(String message, Map data) {
        super(message);
        this.data = data;
    }
}

