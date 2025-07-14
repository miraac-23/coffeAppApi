package com.example.coffeApp.dto.coffe;

import com.example.coffeApp.entity.Ingredient;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoffeDto {

    private Long id;

    private String coffeeName;

    private String preparationTime;

    private String calorie;

    private BigDecimal price;

    @Type(JsonBinaryType.class)
    private List<Ingredient> ingredients;

}
