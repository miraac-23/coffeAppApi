package com.example.coffeApp.controller;

import com.example.coffeApp.dto.coffe.CoffeCreateDto;
import com.example.coffeApp.dto.coffe.CoffeDto;
import com.example.coffeApp.dto.coffe.CoffeOrderResponseDto;
import com.example.coffeApp.service.coffe.CoffeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CoffeControllerTest {

    @Mock
    private CoffeService coffeService;

    @InjectMocks
    private CoffeController coffeController;

    private CoffeDto coffeDto;
    private CoffeCreateDto createDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        createDto = new CoffeCreateDto();
        createDto.setCoffeeName("Espresso");

        coffeDto = new CoffeDto();
        coffeDto.setId(1L);
        coffeDto.setCoffeeName("Espresso");
        coffeDto.setPrice(BigDecimal.valueOf(25));
    }


    @Test
    @DisplayName("createCoffee - başarıyla kahve oluşturulmalı")
    void testCreateCoffeeSuccess() {
        when(coffeService.createCoffee(createDto)).thenReturn(coffeDto);

        ResponseEntity<CoffeDto> response = coffeController.createCoffee(createDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(coffeDto, response.getBody());
        verify(coffeService).createCoffee(createDto);
    }

    @Test
    @DisplayName("createCoffee - servis hata fırlatırsa hata dönmeli")
    void testCreateCoffeeThrowsException() {
        when(coffeService.createCoffee(createDto)).thenThrow(new RuntimeException("creation error"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> coffeController.createCoffee(createDto));
        assertEquals("creation error", ex.getMessage());
    }


    @Test
    @DisplayName("update - başarıyla güncellenmeli")
    void testUpdateSuccess() {
        when(coffeService.update(coffeDto)).thenReturn(coffeDto);

        ResponseEntity<CoffeDto> response = coffeController.update(coffeDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(coffeDto, response.getBody());
        verify(coffeService).update(coffeDto);
    }

    @Test
    @DisplayName("update - servis exception fırlatırsa hata dönmeli")
    void testUpdateThrowsException() {
        when(coffeService.update(coffeDto)).thenThrow(new RuntimeException("update failed"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> coffeController.update(coffeDto));
        assertEquals("update failed", ex.getMessage());
    }


    @Test
    @DisplayName("getById - geçerli id ile kahve bilgilerini dönmeli")
    void testGetByIdSuccess() {
        when(coffeService.getById(1L)).thenReturn(coffeDto);

        ResponseEntity<CoffeDto> response = coffeController.getById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(coffeDto, response.getBody());
        verify(coffeService).getById(1L);
    }

    @Test
    @DisplayName("getById - servis exception fırlatırsa hata dönmeli")
    void testGetByIdThrowsException() {
        when(coffeService.getById(1L)).thenThrow(new RuntimeException("not found"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> coffeController.getById(1L));
        assertEquals("not found", ex.getMessage());
    }


    @Test
    @DisplayName("getAll - kahve listesi dönmeli")
    void testGetAllSuccess() {
        List<CoffeDto> coffeeList = List.of(coffeDto);

        when(coffeService.getAll()).thenReturn(coffeeList);

        ResponseEntity<List<CoffeDto>> response = coffeController.getAll();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("Espresso", response.getBody().get(0).getCoffeeName());
    }

    @Test
    @DisplayName("getAll - servis exception fırlatırsa hata dönmeli")
    void testGetAllThrowsException() {
        when(coffeService.getAll()).thenThrow(new RuntimeException("list error"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> coffeController.getAll());
        assertEquals("list error", ex.getMessage());
    }


    @Test
    @DisplayName("delete - geçerli id ile silme işlemi başarılı")
    void testDeleteSuccess() {
        ResponseEntity<Void> response = coffeController.delete(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(coffeService).delete(1L);
    }

    @Test
    @DisplayName("delete - servis exception fırlatırsa hata dönmeli")
    void testDeleteThrowsException() {
        doThrow(new RuntimeException("delete error")).when(coffeService).delete(1L);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> coffeController.delete(1L));
        assertEquals("delete error", ex.getMessage());
    }


    @Test
    @DisplayName("coffeOrder - geçerli id ile sipariş dönmeli")
    void testCoffeOrderSuccess() {
        CoffeOrderResponseDto orderResponseDto = new CoffeOrderResponseDto();
        orderResponseDto.setPrice(BigDecimal.valueOf(30));

        when(coffeService.coffeOrder(1L)).thenReturn(orderResponseDto);

        ResponseEntity<CoffeOrderResponseDto> response = coffeController.coffeOrder(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(BigDecimal.valueOf(30), response.getBody().getPrice());
        verify(coffeService).coffeOrder(1L);
    }

    @Test
    @DisplayName("coffeOrder - servis exception fırlatırsa hata dönmeli")
    void testCoffeOrderThrowsException() {
        when(coffeService.coffeOrder(1L)).thenThrow(new RuntimeException("order failed"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> coffeController.coffeOrder(1L));
        assertEquals("order failed", ex.getMessage());
    }
}
