package com.example.coffeApp.service.coffe;

import com.example.coffeApp.dto.availableProduct.AvailableProductDto;
import com.example.coffeApp.dto.coffe.CoffeCreateDto;
import com.example.coffeApp.dto.coffe.CoffeDto;
import com.example.coffeApp.dto.coffe.CoffeOrderResponseDto;
import com.example.coffeApp.entity.CoffeEntity;
import com.example.coffeApp.entity.Ingredient;
import com.example.coffeApp.exception.AppException;
import com.example.coffeApp.exception.AppNotFoundException;
import com.example.coffeApp.exception.AppValidationException;
import com.example.coffeApp.mapper.CoffeMapper;
import com.example.coffeApp.repository.CoffeRepository;
import com.example.coffeApp.service.availableProduct.AvailableProductServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class CoffeServiceTest {

    @InjectMocks
    private CoffeServiceImpl coffeService;

    @Mock
    private CoffeRepository coffeRepository;

    @Mock
    private CoffeMapper coffeMapper;

    @Mock
    private AvailableProductServiceImpl availableProductService;


    @Test
    @DisplayName("createCoffee: DTO null ise AppValidationException fırlatılmalı")
    void createCoffee_shouldThrowException_whenDtoIsNull() {
        assertThrows(AppValidationException.class,
                () -> coffeService.createCoffee(null));
    }

    @Test
    @DisplayName("createCoffee: Aynı isimde kahve varsa AppValidationException fırlatılmalı")
    void createCoffee_shouldThrowException_whenCoffeeNameAlreadyExists() {
        CoffeCreateDto coffeCreateDto = new CoffeCreateDto();
        coffeCreateDto.setCoffeeName("Latte");

        when(coffeRepository.findByCoffeeName("Latte"))
                .thenReturn(Optional.of(new CoffeEntity()));

        assertThrows(AppValidationException.class,
                () -> coffeService.createCoffee(coffeCreateDto));
    }

    @Test
    @DisplayName("createCoffee: İçeride beklenmeyen hata oluşursa AppException fırlatılmalı")
    void createCoffee_shouldThrowAppException_whenUnexpectedExceptionOccurs() {
        CoffeCreateDto coffeCreateDto = new CoffeCreateDto();
        coffeCreateDto.setCoffeeName("Mocha");

        when(coffeRepository.findByCoffeeName("Mocha")).thenThrow(new RuntimeException("Veritabanı hatası"));

        AppException ex = assertThrows(AppException.class,
                () -> coffeService.createCoffee(coffeCreateDto));

        assertTrue(ex.getMessage().contains("Veritabanı hatası"));
    }

    @Test
    @DisplayName("createCoffee: Geçerli DTO ile başarılı kahve oluşturulmalı")
    void createCoffee_shouldReturnCoffeeDto_whenSuccessful() {
        CoffeCreateDto coffeCreateDto = new CoffeCreateDto();
        coffeCreateDto.setCoffeeName("Espresso");

        CoffeEntity entity = new CoffeEntity();
        CoffeDto coffeDto = new CoffeDto();

        when(coffeRepository.findByCoffeeName("Espresso")).thenReturn(Optional.empty());
        when(coffeMapper.createDtoToEntity(coffeCreateDto)).thenReturn(entity);
        when(coffeRepository.save(entity)).thenReturn(entity);
        when(coffeMapper.entityToDto(entity)).thenReturn(coffeDto);

        CoffeDto result = coffeService.createCoffee(coffeCreateDto);
        assertNotNull(result);
    }

    @Test
    @DisplayName("update: DTO null ise AppValidationException fırlatılmalı")
    void update_shouldThrowException_whenDtoIsNull() {
        assertThrows(AppValidationException.class,
                () -> coffeService.update(null));
    }

    @Test
    @DisplayName("update: Verilen ID ile kahve bulunamazsa AppNotFoundException fırlatılmalı")
    void update_shouldThrowException_whenCoffeeNotFound() {
        CoffeDto coffeDto = new CoffeDto();
        coffeDto.setId(1L);

        when(coffeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(AppNotFoundException.class,
                () -> coffeService.update(coffeDto));
    }

    @Test
    @DisplayName("update: İçeride beklenmeyen hata oluşursa AppException fırlatılmalı")
    void update_shouldThrowAppException_whenUnexpectedExceptionOccurs() {
        CoffeDto coffeDto = new CoffeDto();
        coffeDto.setId(1L);

        when(coffeRepository.findById(1L)).thenReturn(Optional.of(new CoffeEntity()));
        doThrow(new RuntimeException("Mapping hatası"))
                .when(coffeMapper).updateDtoToEntity(any(), any());

        AppException ex = assertThrows(AppException.class,
                () -> coffeService.update(coffeDto));

        assertTrue(ex.getMessage().contains("Mapping hatası"));
    }

    @Test
    @DisplayName("update: Geçerli DTO ile kahve güncellenmeli")
    void update_shouldReturnUpdatedDto_whenSuccessful() {
        CoffeDto coffeDto = new CoffeDto();
        coffeDto.setId(1L);

        CoffeEntity entity = new CoffeEntity();

        when(coffeRepository.findById(1L)).thenReturn(Optional.of(entity));
        doNothing().when(coffeMapper).updateDtoToEntity(coffeDto, entity);
        when(coffeRepository.save(entity)).thenReturn(entity);
        when(coffeMapper.entityToDto(entity)).thenReturn(coffeDto);

        CoffeDto result = coffeService.update(coffeDto);
        assertNotNull(result);
    }

    @Test
    @DisplayName("getById: ID null ise AppValidationException fırlatılmalı")
    void getById_shouldThrowException_whenIdIsNull() {
        assertThrows(AppValidationException.class,
                () -> coffeService.getById(null));
    }

    @Test
    @DisplayName("getById: Verilen ID ile kahve bulunamazsa AppNotFoundException fırlatılmalı")
    void getById_shouldThrowException_whenNotFound() {
        when(coffeRepository.findById(5L)).thenReturn(Optional.empty());

        assertThrows(AppNotFoundException.class,
                () -> coffeService.getById(5L));
    }

    @Test
    @DisplayName("getById: İçeride beklenmeyen hata oluşursa AppException fırlatılmalı")
    void getById_shouldThrowAppException_whenUnexpectedExceptionOccurs() {
        when(coffeRepository.findById(1L)).thenThrow(new RuntimeException("Repo hatası"));

        AppException ex = assertThrows(AppException.class,
                () -> coffeService.getById(1L));

        assertTrue(ex.getMessage().contains("Repo hatası"));
    }

    @Test
    @DisplayName("getById: Mevcut ID ile kahve döndürülmeli")
    void getById_shouldReturnCoffeeDto_whenFound() {
        CoffeEntity coffeEntity = new CoffeEntity();
        CoffeDto coffeDto = new CoffeDto();

        when(coffeRepository.findById(1L)).thenReturn(Optional.of(coffeEntity));
        when(coffeMapper.entityToDto(coffeEntity)).thenReturn(coffeDto);

        CoffeDto result = coffeService.getById(1L);
        assertNotNull(result);
    }


    @Test
    @DisplayName("getAll: Veri yoksa AppNotFoundException fırlatılmalı")
    void getAll_shouldThrowException_whenEmptyList() {
        when(coffeRepository.findAll()).thenReturn(List.of());

        assertThrows(AppNotFoundException.class,
                () -> coffeService.getAll());
    }


    @Test
    @DisplayName("getAll: Veri varsa CoffeDto listesi döndürülmeli")
    void getAll_shouldReturnList_whenDataExists() {
        CoffeEntity coffeEntity = new CoffeEntity();
        CoffeDto coffeDto = new CoffeDto();

        when(coffeRepository.findAll()).thenReturn(List.of(coffeEntity));
        when(coffeMapper.entityToDto(coffeEntity)).thenReturn(coffeDto);

        List<CoffeDto> result = coffeService.getAll();
        assertEquals(1, result.size());
    }


    @Test
    @DisplayName("delete: ID null ise AppValidationException fırlatılmalı")
    void delete_shouldThrowException_whenIdIsNull() {
        assertThrows(AppValidationException.class,
                () -> coffeService.delete(null));
    }

    @Test
    @DisplayName("delete: ID ile kahve bulunamazsa AppNotFoundException fırlatılmalı")
    void delete_shouldThrowException_whenNotFound() {
        when(coffeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(AppNotFoundException.class,
                () -> coffeService.delete(1L));
    }

    @Test
    @DisplayName("delete: İçeride beklenmeyen hata oluşursa AppException fırlatılmalı")
    void delete_shouldThrowAppException_whenUnexpectedExceptionOccurs() {
        when(coffeRepository.findById(1L)).thenReturn(Optional.of(new CoffeEntity()));
        doThrow(new RuntimeException("Silme hatası"))
                .when(coffeRepository).deleteById(1L);

        AppException ex = assertThrows(AppException.class,
                () -> coffeService.delete(1L));

        assertTrue(ex.getMessage().contains("Silme hatası"));
    }


    @Test
    @DisplayName("delete: Geçerli ID ile kahve silinmeli")
    void delete_shouldCallRepositoryDelete_whenSuccessful() {
        CoffeEntity coffeEntity = new CoffeEntity();
        when(coffeRepository.findById(1L)).thenReturn(Optional.of(coffeEntity));
        doNothing().when(coffeRepository).deleteById(1L);

        assertDoesNotThrow(() -> coffeService.delete(1L));
        Mockito.verify(coffeRepository).deleteById(1L);
    }


    @Test
    @DisplayName("ID null olduğunda AppValidationException fırlatılmalı")
    void testOrderWithNullIdThrowsValidationException() {
        assertThrows(AppValidationException.class, () -> coffeService.coffeOrder(null));
    }


    @Test
    @DisplayName("Geçersiz ID ile kahve bulunamazsa AppNotFoundException fırlatılmalı")
    void testOrderWithInvalidIdThrowsNotFoundException() {
        Long invalidId = 99L;
        when(coffeRepository.findById(invalidId)).thenReturn(Optional.empty());

        assertThrows(AppNotFoundException.class, () -> coffeService.coffeOrder(invalidId));
    }


    //TODO Kontrol edilecek
    /*@Test
    @DisplayName("coffeOrder: Ingredient işlenirken AppException fırlatılmalı")
    void coffeOrder_shouldThrowAppException_whenIngredientProcessingFails() {
        Long id = 1L;

        CoffeDto dto = new CoffeDto();
        dto.setCoffeeName("Latte");
        dto.setPrice(BigDecimal.TEN);

        Ingredient ingredient = new Ingredient();
        ingredient.setProductId(999L);
        ingredient.setAmount("1");
        dto.setIngredients(List.of(ingredient));

        when(coffeRepository.findById(id)).thenReturn(Optional.of(new CoffeEntity()));
        when(coffeMapper.entityToDto(any())).thenReturn(dto);

        when(availableProductService.getById(999L))
                .thenThrow(new RuntimeException("Servis çökmesi"));

    }*/

    @Test
    @DisplayName("coffeOrder: Geçerli ID ile kahve sipariş bilgisi dönmeli")
    void coffeOrder_shouldReturnOrderResponse_whenIngredientsFound() {
        Long id = 1L;

        CoffeDto coffeDto = new CoffeDto();
        coffeDto.setCoffeeName("Mocha");
        coffeDto.setPrice(BigDecimal.TEN);

        Ingredient ingredient = new Ingredient();
        ingredient.setProductId(99L);
        ingredient.setAmount("2");
        coffeDto.setIngredients(List.of(ingredient));

        AvailableProductDto productDto = new AvailableProductDto();
        productDto.setProductName("Milk");

        when(coffeRepository.findById(id)).thenReturn(Optional.of(new CoffeEntity()));
        when(coffeMapper.entityToDto(any())).thenReturn(coffeDto);
        when(availableProductService.getById(99L)).thenReturn(productDto);

        CoffeOrderResponseDto response = coffeService.coffeOrder(id);
        assertNotNull(response);
        assertEquals("Mocha", response.getCoffeeName());
        assertEquals(1, response.getItems().size());
    }


}
