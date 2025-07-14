package com.example.coffeApp.mapper;

import com.example.coffeApp.dto.coffe.CoffeCreateDto;
import com.example.coffeApp.dto.coffe.CoffeDto;
import com.example.coffeApp.entity.CoffeEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CoffeMapper {

    CoffeEntity dtoToEntity(CoffeDto coffeDto);

    CoffeDto entityToDto(CoffeEntity coffeEntity);

    CoffeEntity createDtoToEntity(CoffeCreateDto coffeCreateDto);

    void updateDtoToEntity(CoffeDto coffeDto, @MappingTarget CoffeEntity coffeEntity);

}
