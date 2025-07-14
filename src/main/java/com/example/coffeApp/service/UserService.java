package com.example.coffeApp.service;


import com.example.coffeApp.dto.user.UserAddDto;
import com.example.coffeApp.dto.user.UserResultDto;

import java.util.List;

public interface UserService {

    UserResultDto getUserById(Integer id);

    UserResultDto userAdd(UserAddDto userAddDto);

    UserResultDto getUserByEmail(String email);

    List<UserResultDto> getAllUer();

    void delete(Integer id);


}
