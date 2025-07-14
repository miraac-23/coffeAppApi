package com.example.coffeApp.mapper;


import com.example.coffeApp.dto.user.UserAddDto;
import com.example.coffeApp.dto.user.UserResultDto;
import com.example.coffeApp.entity.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResultDto entityToDto(UserEntity userEntity);

    UserEntity dtoToEntity(UserResultDto userResultDto);

    UserEntity addDtoToEntity(UserAddDto userAddDto);

}
