package com.example.coffeApp.dto.user;

import lombok.Data;

@Data
public class UserDto {

    private Integer id;

    private String name;

    private String surname;

    private String password;

    private String tcNo;

    private String email;

    private String userType;

}
