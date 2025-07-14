package com.example.coffeApp.service;

import com.example.coffeApp.dto.user.UserAddDto;
import com.example.coffeApp.dto.user.UserResultDto;
import com.example.coffeApp.entity.UserEntity;
import com.example.coffeApp.exception.AppException;
import com.example.coffeApp.exception.AppNotFoundException;
import com.example.coffeApp.exception.AppValidationException;
import com.example.coffeApp.mapper.UserMapper;
import com.example.coffeApp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    private UserEntity userEntity;
    private UserResultDto userResultDto;
    private UserAddDto userAddDto;

    @BeforeEach
    void setup() {
        userEntity = new UserEntity();
        userEntity.setId(1);
        userEntity.setEmail("test@example.com");
        userEntity.setPassword("encodedPass");

        userResultDto = new UserResultDto();
        userResultDto.setId(1);
        userResultDto.setEmail("test@example.com");

        userAddDto = new UserAddDto();
        userAddDto.setEmail("test@example.com");
        userAddDto.setPassword("123456");
    }


    @Test
    @DisplayName("getUserById - null id ile AppValidationException fırlatılmalı")
    void testGetUserByIdWithNullId() {
        assertThrows(AppValidationException.class, () -> userService.getUserById(null));
    }

    @Test
    @DisplayName("getUserById - geçerli id ile başarılı şekilde kullanıcı dönmeli")
    void testGetUserByIdSuccess() {
        when(userRepository.findById(1)).thenReturn(Optional.of(userEntity));
        when(userMapper.entityToDto(userEntity)).thenReturn(userResultDto);

        UserResultDto result = userService.getUserById(1);

        assertEquals(userResultDto.getId(), result.getId());
        verify(userRepository).findById(1);
    }

    @Test
    @DisplayName("getUserById - beklenmeyen hata olursa AppException fırlatılmalı")
    void testGetUserByIdUnexpectedException() {
        when(userRepository.findById(1)).thenThrow(new RuntimeException("Database error"));

        AppException ex = assertThrows(AppException.class, () -> userService.getUserById(1));
        assertTrue(ex.getMessage().contains("Database error"));
    }

    @Test
    @DisplayName("getUserById - kullanıcı bulunamazsa AppNotFoundException fırlatılmalı")
    void testGetUserByIdNotFound() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(AppNotFoundException.class, () -> userService.getUserById(1));
    }


    @Test
    @DisplayName("userAdd - null dto ile AppValidationException fırlatılmalı")
    void testUserAddWithNullDto() {
        assertThrows(AppValidationException.class, () -> userService.userAdd(null));
    }

    @Test
    @DisplayName("userAdd - mevcut email ile kayıtlı kullanıcı varsa AppValidationException fırlatılmalı")
    void testUserAddWithExistingEmail() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(userEntity));

        assertThrows(AppValidationException.class, () -> userService.userAdd(userAddDto));
    }

    @Test
    @DisplayName("userAdd - beklenmeyen hata olursa AppException fırlatılmalı")
    void testUserAddUnexpectedException() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(userMapper.addDtoToEntity(any())).thenThrow(new RuntimeException("Mapping failed"));

        AppException ex = assertThrows(AppException.class, () -> userService.userAdd(userAddDto));
        assertTrue(ex.getMessage().contains("Mapping failed"));
    }

    @Test
    @DisplayName("userAdd - yeni kullanıcı başarılı şekilde eklenmeli")
    void testUserAddSuccess() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(userMapper.addDtoToEntity(any(UserAddDto.class))).thenReturn(userEntity);
        when(userMapper.entityToDto(userEntity)).thenReturn(userResultDto);

        UserResultDto result = userService.userAdd(userAddDto);

        assertEquals("test@example.com", result.getEmail());
        verify(userRepository).save(any(UserEntity.class));
    }


    @Test
    @DisplayName("getUserByEmail - null email ile AppValidationException fırlatılmalı")
    void testGetUserByEmailNull() {
        assertThrows(AppValidationException.class, () -> userService.getUserByEmail(null));
    }

    @Test
    @DisplayName("getUserByEmail - email bulunamazsa AppNotFoundException fırlatılmalı")
    void testGetUserByEmailNotFound() {
        when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        assertThrows(AppNotFoundException.class, () -> userService.getUserByEmail("notfound@example.com"));
    }

    @Test
    @DisplayName("getUserByEmail - beklenmeyen hata olursa AppException fırlatılmalı")
    void testGetUserByEmailUnexpectedException() {
        when(userRepository.findByEmail("test@example.com")).thenThrow(new RuntimeException("Unexpected"));

        AppException ex = assertThrows(AppException.class, () -> userService.getUserByEmail("test@example.com"));
        assertTrue(ex.getMessage().contains("Unexpected"));
    }

    @Test
    @DisplayName("getUserByEmail - geçerli email ile kullanıcı dönmeli")
    void testGetUserByEmailSuccess() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(userEntity));
        when(userMapper.entityToDto(userEntity)).thenReturn(userResultDto);

        UserResultDto result = userService.getUserByEmail("test@example.com");

        assertEquals("test@example.com", result.getEmail());
    }


    @Test
    @DisplayName("getAllUser - veri yoksa AppNotFoundException fırlatılmalı")
    void testGetAllUserEmpty() {
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        assertThrows(AppNotFoundException.class, () -> userService.getAllUer());
    }

    @Test
    @DisplayName("getAllUser - kullanıcı listesi dönmeli")
    void testGetAllUserSuccess() {
        List<UserEntity> userEntities = List.of(userEntity);
        when(userRepository.findAll()).thenReturn(userEntities);
        when(userMapper.entityToDto(userEntity)).thenReturn(userResultDto);

        List<UserResultDto> result = userService.getAllUer();

        assertEquals(1, result.size());
    }


    @Test
    @DisplayName("delete - null id ile AppValidationException fırlatılmalı")
    void testDeleteWithNullId() {
        assertThrows(AppValidationException.class, () -> userService.delete(null));
    }

    @Test
    @DisplayName("delete - kullanıcı bulunamazsa AppNotFoundException fırlatılmalı")
    void testDeleteNotFound() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(AppNotFoundException.class, () -> userService.delete(1));
    }

    @Test
    @DisplayName("delete - beklenmeyen hata olursa AppException fırlatılmalı")
    void testDeleteUnexpectedException() {
        when(userRepository.findById(1)).thenThrow(new RuntimeException("Unexpected error"));

        AppException ex = assertThrows(AppException.class, () -> userService.delete(1));
        assertTrue(ex.getMessage().contains("Unexpected error"));
    }

    @Test
    @DisplayName("delete - kullanıcı başarıyla silinmeli")
    void testDeleteSuccess() {
        when(userRepository.findById(1)).thenReturn(Optional.of(userEntity));

        userService.delete(1);

        verify(userRepository).deleteById(1);
    }
}
