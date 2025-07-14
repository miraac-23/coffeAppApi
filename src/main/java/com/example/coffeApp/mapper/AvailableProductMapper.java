package com.example.coffeApp.mapper;

import com.example.coffeApp.dto.availableProduct.AvailableProductCreateDto;
import com.example.coffeApp.dto.availableProduct.AvailableProductDto;
import com.example.coffeApp.entity.AvailableProductEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AvailableProductMapper {
    AvailableProductEntity dtoToEntity(AvailableProductDto availableProductDto);
    AvailableProductDto entityToDto(AvailableProductEntity availableProductEntity);
    AvailableProductEntity createDtoToEntity(AvailableProductCreateDto availableProductCreateDto);

}
