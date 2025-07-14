package com.example.coffeApp.filter;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class LoggerDetail {
    public Map<String, String> values = new HashMap<>();
}
