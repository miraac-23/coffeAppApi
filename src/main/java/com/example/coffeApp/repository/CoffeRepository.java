package com.example.coffeApp.repository;

import com.example.coffeApp.entity.CoffeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CoffeRepository extends JpaRepository<CoffeEntity, Long> {

    Optional<CoffeEntity> findByCoffeeName(String coffeName);

}
