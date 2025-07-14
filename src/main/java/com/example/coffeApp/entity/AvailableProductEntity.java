package com.example.coffeApp.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "available_product")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvailableProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String productName;
}
