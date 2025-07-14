package com.example.coffeApp.dto.coffe;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoffeeOrderItemDto {

    private String productName;

    private String amount;

}
