package com.example.coffeApp.dto.coffe;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoffeOrderResponseDto {

    private String coffeeName;

    private BigDecimal price;

    private String preparationTime;

    private String calorie;

    private List<CoffeeOrderItemDto> items;

}
