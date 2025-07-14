package com.example.coffeApp.controller;

import com.example.coffeApp.dto.availableProduct.AvailableProductCreateDto;
import com.example.coffeApp.dto.availableProduct.AvailableProductDto;
import com.example.coffeApp.exception.AppNotFoundException;
import com.example.coffeApp.service.availableProduct.AvailableProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AvailableProductControllerTest {

    @Mock
    private AvailableProductService availableProductService;

    @InjectMocks
    private AvailableProductController controller;

    private AvailableProductDto availableProductDto;
    private AvailableProductCreateDto createDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        availableProductDto = new AvailableProductDto();
        availableProductDto.setId(1L);
        availableProductDto.setProductName("Coffee Beans");

        createDto = new AvailableProductCreateDto();
        createDto.setProductName("Coffee Beans");
    }


    @Test
    @DisplayName("create - başarılı şekilde ürün oluşturmalı")
    void testCreateSuccess() {
        when(availableProductService.create(createDto)).thenReturn(availableProductDto);

        ResponseEntity<AvailableProductDto> response = controller.create(createDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(availableProductDto, response.getBody());
        verify(availableProductService).create(createDto);
    }

    @Test
    @DisplayName("create - servis exception fırlatırsa hata dönmeli")
    void testCreateThrowsException() {
        when(availableProductService.create(createDto)).thenThrow(new RuntimeException("error"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> controller.create(createDto));
        assertEquals("error", ex.getMessage());
    }


    @Test
    @DisplayName("update - başarılı şekilde güncellemeli")
    void testUpdateSuccess() {
        when(availableProductService.update(availableProductDto)).thenReturn(availableProductDto);

        ResponseEntity<AvailableProductDto> response = controller.update(availableProductDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(availableProductDto, response.getBody());
        verify(availableProductService).update(availableProductDto);
    }

    @Test
    @DisplayName("update - servis exception fırlatırsa hata dönmeli")
    void testUpdateThrowsException() {
        when(availableProductService.update(availableProductDto)).thenThrow(new RuntimeException("update error"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> controller.update(availableProductDto));
        assertEquals("update error", ex.getMessage());
    }


    @Test
    @DisplayName("getById - geçerli id ile ürün dönmeli")
    void testGetByIdSuccess() {
        when(availableProductService.getById(1L)).thenReturn(availableProductDto);

        ResponseEntity<AvailableProductDto> response = controller.getById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(availableProductDto, response.getBody());
        verify(availableProductService).getById(1L);
    }

    @Test
    @DisplayName("getById - ürün bulunamazsa AppNotFoundException fırlatılmalı")
    void testGetByIdNotFound() {
        when(availableProductService.getById(1L)).thenThrow(new AppNotFoundException("bulunamadı"));

        AppNotFoundException ex = assertThrows(AppNotFoundException.class, () -> controller.getById(1L));
        assertEquals("bulunamadı", ex.getMessage());
    }


    @Test
    @DisplayName("getAll - ürün listesi dönmeli")
    void testGetAllSuccess() {
        List<AvailableProductDto> dtoList = List.of(availableProductDto);

        when(availableProductService.getAll()).thenReturn(dtoList);

        ResponseEntity<List<AvailableProductDto>> response = controller.getAll();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("Coffee Beans", response.getBody().get(0).getProductName());
    }

    @Test
    @DisplayName("getAll - servis exception fırlatırsa hata dönmeli")
    void testGetAllThrowsException() {
        when(availableProductService.getAll()).thenThrow(new RuntimeException("fetch failed"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> controller.getAll());
        assertEquals("fetch failed", ex.getMessage());
    }


    @Test
    @DisplayName("delete - geçerli id ile silme işlemi başarılı")
    void testDeleteSuccess() {
        ResponseEntity<Void> response = controller.delete(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(availableProductService).delete(1L);
    }

    @Test
    @DisplayName("delete - servis exception fırlatırsa hata dönmeli")
    void testDeleteThrowsException() {
        doThrow(new RuntimeException("delete failed")).when(availableProductService).delete(1L);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> controller.delete(1L));
        assertEquals("delete failed", ex.getMessage());
    }
}
