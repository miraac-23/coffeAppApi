package com.example.coffeApp.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppErrorResponse {

    private int status;

    private String message;

    private Map<String, Object> fields;

}
