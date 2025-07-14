package com.example.coffeApp.dto.availableProduct;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AvailableProductDto {

    private Long id;

    private String productName;

}
