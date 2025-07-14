package com.example.coffeApp.service.coffe;

import com.example.coffeApp.dto.coffe.CoffeCreateDto;
import com.example.coffeApp.dto.coffe.CoffeDto;
import com.example.coffeApp.dto.coffe.CoffeOrderResponseDto;

import java.util.List;

public interface CoffeService {

    CoffeDto createCoffee(CoffeCreateDto createDto);

    CoffeDto update(CoffeDto coffeDto);

    CoffeDto getById(Long id);

    List<CoffeDto> getAll();

    void delete(Long id);

    CoffeOrderResponseDto coffeOrder(Long id);


}
