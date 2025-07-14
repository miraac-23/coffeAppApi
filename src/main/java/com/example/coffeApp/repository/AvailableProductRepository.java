package com.example.coffeApp.repository;

import com.example.coffeApp.entity.AvailableProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AvailableProductRepository extends JpaRepository<AvailableProductEntity ,Long> {

    Optional<AvailableProductEntity> findByProductName(String productName);

}
