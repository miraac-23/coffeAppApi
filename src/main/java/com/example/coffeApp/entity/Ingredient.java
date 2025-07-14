package com.example.coffeApp.entity;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ingredient {

    private Long productId;

    private String amount;

}
