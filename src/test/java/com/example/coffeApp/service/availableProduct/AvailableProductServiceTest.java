package com.example.coffeApp.service.availableProduct;

import com.example.coffeApp.dto.availableProduct.AvailableProductCreateDto;
import com.example.coffeApp.dto.availableProduct.AvailableProductDto;
import com.example.coffeApp.entity.AvailableProductEntity;
import com.example.coffeApp.exception.AppException;
import com.example.coffeApp.exception.AppNotFoundException;
import com.example.coffeApp.exception.AppValidationException;
import com.example.coffeApp.mapper.AvailableProductMapper;
import com.example.coffeApp.repository.AvailableProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AvailableProductServiceTest {

    @InjectMocks
    private AvailableProductServiceImpl availableProductService;

    @Mock
    private AvailableProductRepository availableProductRepository;

    @Mock
    private AvailableProductMapper availableProductMapper;


    @Test
    @DisplayName("create: DTO null ise AppValidationException fırlatılmalı")
    void create_shouldThrowAppValidationException_whenDtoIsNull() {
        assertThrows(AppValidationException.class, () -> availableProductService.create(null));
    }

    @Test
    @DisplayName("create: Aynı isimde ürün varsa AppValidationException fırlatılmalı")
    void create_shouldThrowAppValidationException_whenProductNameAlreadyExists() {
        AvailableProductCreateDto availableProductCreateDto = new AvailableProductCreateDto();
        availableProductCreateDto.setProductName("Coffee");

        when(availableProductRepository.findByProductName("Coffee"))
                .thenReturn(Optional.of(new AvailableProductEntity()));

        assertThrows(AppValidationException.class, () -> availableProductService.create(availableProductCreateDto));
    }

    @Test
    @DisplayName("create: Beklenmeyen hata olursa AppException fırlatılmalı")
    void create_shouldThrowAppException_whenUnexpectedExceptionOccurs() {
        AvailableProductCreateDto availableProductCreateDto = new AvailableProductCreateDto();
        availableProductCreateDto.setProductName("Espresso");

        when(availableProductRepository.findByProductName("Espresso")).thenThrow(new RuntimeException("DB error"));

        AppException exception = assertThrows(AppException.class, () -> availableProductService.create(availableProductCreateDto));
        assertEquals("DB error", exception.getMessage());
    }


    @Test
    @DisplayName("create: Geçerli veriyle ürün başarıyla oluşturulmalı")
    void create_shouldCreateProductSuccessfully() {
        AvailableProductCreateDto availableProductCreateDto = new AvailableProductCreateDto();
        availableProductCreateDto.setProductName("Latte");

        AvailableProductEntity entity = new AvailableProductEntity();
        entity.setProductName("Latte");

        AvailableProductDto resultDto = new AvailableProductDto();
        resultDto.setProductName("Latte");

        when(availableProductRepository.findByProductName("Latte")).thenReturn(Optional.empty());
        when(availableProductMapper.createDtoToEntity(availableProductCreateDto)).thenReturn(entity);
        when(availableProductMapper.entityToDto(entity)).thenReturn(resultDto);

        AvailableProductDto result = availableProductService.create(availableProductCreateDto);

        assertNotNull(result);
        assertEquals("Latte", result.getProductName());
    }


    @Test
    @DisplayName("update: DTO null ise AppValidationException fırlatılmalı")
    void update_shouldThrowAppValidationException_whenDtoIsNull() {
        assertThrows(AppValidationException.class, () -> availableProductService.update(null));
    }

    @Test
    @DisplayName("update: Ürün bulunamazsa AppNotFoundException fırlatılmalı")
    void update_shouldThrowAppNotFoundException_whenProductNotFound() {
        AvailableProductDto availableProductDto = new AvailableProductDto();
        availableProductDto.setId(1L);

        when(availableProductRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(AppNotFoundException.class, () -> availableProductService.update(availableProductDto));
    }

    @Test
    @DisplayName("update: Beklenmeyen hata olursa AppException fırlatılmalı")
    void update_shouldThrowAppException_whenUnexpectedExceptionOccurs() {
        AvailableProductDto availableProductDto = new AvailableProductDto();
        availableProductDto.setId(1L);

        when(availableProductRepository.findById(1L)).thenThrow(new RuntimeException("Unexpected"));

        AppException exception = assertThrows(AppException.class, () -> availableProductService.update(availableProductDto));
        assertEquals("Unexpected", exception.getMessage());
    }

    @Test
    @DisplayName("update: Geçerli veriyle ürün başarıyla güncellenmeli")
    void update_shouldUpdateProductSuccessfully() {
        AvailableProductDto availableProductDto = new AvailableProductDto();
        availableProductDto.setId(1L);
        availableProductDto.setProductName("Cappuccino");

        AvailableProductEntity entity = new AvailableProductEntity();
        entity.setId(1L);

        AvailableProductDto updatedDto = new AvailableProductDto();
        updatedDto.setId(1L);
        updatedDto.setProductName("Cappuccino");

        when(availableProductRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(availableProductMapper.entityToDto(entity)).thenReturn(updatedDto);

        AvailableProductDto result = availableProductService.update(availableProductDto);

        assertEquals("Cappuccino", result.getProductName());
    }


    @Test
    @DisplayName("delete: ID null ise AppValidationException fırlatılmalı")
    void delete_shouldThrowAppValidationException_whenIdIsNull() {
        assertThrows(AppValidationException.class, () -> availableProductService.delete(null));
    }

    @Test
    @DisplayName("delete: ID'ye ait ürün bulunamazsa AppNotFoundException fırlatılmalı")
    void delete_shouldThrowAppNotFoundException_whenProductNotFound() {
        when(availableProductRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(AppNotFoundException.class, () -> availableProductService.delete(1L));
    }

    @Test
    @DisplayName("delete: Beklenmeyen hata olursa AppException fırlatılmalı")
    void delete_shouldThrowAppException_whenUnexpectedExceptionOccurs() {
        when(availableProductRepository.findById(1L)).thenThrow(new RuntimeException("Something went wrong"));

        AppException exception = assertThrows(AppException.class, () -> availableProductService.delete(1L));
        assertEquals("Something went wrong", exception.getMessage());
    }


    @Test
    @DisplayName("delete: Geçerli ID ile ürün başarıyla silinmeli")
    void delete_shouldDeleteProductSuccessfully() {
        AvailableProductEntity entity = new AvailableProductEntity();
        entity.setId(1L);

        when(availableProductRepository.findById(1L)).thenReturn(Optional.of(entity));

        assertDoesNotThrow(() -> availableProductService.delete(1L));
        Mockito.verify(availableProductRepository).deleteById(1L);
    }


    @Test
    @DisplayName("getById: ID null ise AppValidationException fırlatılmalı")
    void getById_shouldThrowAppValidationException_whenIdIsNull() {
        assertThrows(AppValidationException.class, () -> availableProductService.getById(null));
    }

    @Test
    @DisplayName("getById: Ürün bulunamazsa AppNotFoundException fırlatılmalı")
    void getById_shouldThrowAppNotFoundException_whenProductNotFound() {
        when(availableProductRepository.findById(5L)).thenReturn(Optional.empty());

        assertThrows(AppNotFoundException.class, () -> availableProductService.getById(5L));
    }

    @Test
    @DisplayName("getById: Beklenmeyen hata olursa AppException fırlatılmalı")
    void getById_shouldThrowAppException_whenUnexpectedExceptionOccurs() {
        when(availableProductRepository.findById(1L)).thenThrow(new RuntimeException("Get failed"));

        AppException exception = assertThrows(AppException.class, () -> availableProductService.getById(1L));
        assertEquals("Get failed", exception.getMessage());
    }


    @Test
    @DisplayName("getById: Geçerli ID ile ürün başarıyla dönmeli")
    void getById_shouldReturnProductSuccessfully() {
        AvailableProductEntity entity = new AvailableProductEntity();
        entity.setId(1L);

        AvailableProductDto availableProductDto = new AvailableProductDto();
        availableProductDto.setId(1L);

        when(availableProductRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(availableProductMapper.entityToDto(entity)).thenReturn(availableProductDto);

        AvailableProductDto result = availableProductService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }


    @Test
    @DisplayName("getAll: Kayıt yoksa AppNotFoundException fırlatılmalı")
    void getAll_shouldThrowAppNotFoundException_whenEmptyList() {
        when(availableProductRepository.findAll()).thenReturn(Collections.emptyList());

        assertThrows(AppNotFoundException.class, () -> availableProductService.getAll());
    }

    @Test
    @DisplayName("getAll: Repository findAll çağrısında hata oluşursa AppException fırlatılmalı")
    void getAll_shouldThrowAppException_whenRepositoryThrowsException() {
        when(availableProductRepository.findAll()).thenThrow(new RuntimeException("Repository error"));

        AppException exception = assertThrows(AppException.class, () -> availableProductService.getAll());
        assertEquals("Repository error", exception.getMessage());
    }


    @Test
    @DisplayName("getAll: Ürün listesi başarıyla dönmeli")
    void getAll_shouldReturnProductListSuccessfully() {
        AvailableProductEntity entity = new AvailableProductEntity();
        entity.setId(1L);

        AvailableProductDto availableProductDto = new AvailableProductDto();
        availableProductDto.setId(1L);

        when(availableProductRepository.findAll()).thenReturn(List.of(entity));
        when(availableProductMapper.entityToDto(entity)).thenReturn(availableProductDto);

        List<AvailableProductDto> result = availableProductService.getAll();

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }

}
