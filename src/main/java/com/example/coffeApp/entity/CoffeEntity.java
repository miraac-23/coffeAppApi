package com.example.coffeApp.entity;

import jakarta.persistence.*;
import lombok.*;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import org.hibernate.annotations.Type;


import java.math.BigDecimal;
import java.util.List;


@Entity
@Table(name = "coffee")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoffeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String coffeeName;

    private String preparationTime;

    private String calorie;

    private BigDecimal price;

    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb")
    private List<Ingredient> ingredients;
}
