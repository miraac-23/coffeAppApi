package com.example.coffeApp.service.availableProduct;

import com.example.coffeApp.dto.availableProduct.AvailableProductCreateDto;
import com.example.coffeApp.dto.availableProduct.AvailableProductDto;

import java.util.List;

public interface AvailableProductService {
    AvailableProductDto create(AvailableProductCreateDto availableProductCreateDto);
    AvailableProductDto update(AvailableProductDto availableProductDto);
    void delete(Long id);
    AvailableProductDto getById(Long id);
    List<AvailableProductDto> getAll();

}
