package com.example.coffeApp.service.coffe;

import com.example.coffeApp.dto.availableProduct.AvailableProductDto;
import com.example.coffeApp.dto.coffe.CoffeCreateDto;
import com.example.coffeApp.dto.coffe.CoffeDto;
import com.example.coffeApp.dto.coffe.CoffeOrderResponseDto;
import com.example.coffeApp.dto.coffe.CoffeeOrderItemDto;
import com.example.coffeApp.entity.CoffeEntity;
import com.example.coffeApp.entity.Ingredient;
import com.example.coffeApp.exception.AppException;
import com.example.coffeApp.exception.AppNotFoundException;
import com.example.coffeApp.exception.AppValidationException;
import com.example.coffeApp.mapper.CoffeMapper;
import com.example.coffeApp.repository.CoffeRepository;
import com.example.coffeApp.service.availableProduct.AvailableProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CoffeServiceImpl implements CoffeService {

    private final CoffeRepository coffeRepository;
    private final CoffeMapper coffeMapper;
    private final AvailableProductService availableProductService;

    @Override
    public CoffeDto createCoffee(CoffeCreateDto coffeCreateDto) {
        try {
            if (coffeCreateDto == null) {
                throw new AppValidationException("Kahve bilgileri boş olamaz.");
            }

            Optional<CoffeEntity> optionalCoffeEntity = coffeRepository.findByCoffeeName(coffeCreateDto.getCoffeeName());

            if (optionalCoffeEntity.isPresent()) {
                throw new AppValidationException("Aynı isimde kahve eklemesi yapılamamaktadır.");
            }

            CoffeEntity coffeEntity = coffeMapper.createDtoToEntity(coffeCreateDto);
            coffeRepository.save(coffeEntity);

            return coffeMapper.entityToDto(coffeEntity);

        } catch (AppException | AppValidationException e) {
            throw e;
        } catch (Exception e) {
            throw new AppException(e.getMessage(), e);
        }
    }

    @Override
    public CoffeDto update(CoffeDto coffeDto) {
        try {
            if (coffeDto == null) {
                throw new AppValidationException("Kahve bilgileri boş olamaz");
            }

            CoffeEntity coffeEntity = coffeRepository.findById(coffeDto.getId())
                    .orElseThrow(() -> new AppNotFoundException("Kahve bulunamadı"));

            coffeMapper.updateDtoToEntity(coffeDto, coffeEntity);

            coffeRepository.save(coffeEntity);
            return coffeMapper.entityToDto(coffeEntity);
        } catch (AppValidationException | AppNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new AppException(e.getMessage(), e);
        }
    }

    @Override
    public CoffeDto getById(Long id) {
        try {
            validateId(id);
            CoffeEntity coffeEntity = coffeRepository.findById(id)
                    .orElseThrow(() -> new AppNotFoundException(id + " id'sine sahip bir kahve sistemde mevcut değil"));

            return coffeMapper.entityToDto(coffeEntity);
        } catch (AppValidationException | AppNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new AppException(e.getMessage(), e);
        }
    }

    @Override
    public List<CoffeDto> getAll() {
        List<CoffeDto> coffeDtoList = coffeRepository.findAll().stream()
                .map(coffeMapper::entityToDto)
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(coffeDtoList)) {

            throw new AppNotFoundException("Veri tabanında kahve kaydına rastlanılmamıştır");

        }

        return coffeDtoList;
    }


    @Override
    public void delete(Long id) {

        try {
            validateId(id);
            coffeRepository.findById(id)
                    .orElseThrow(() -> new AppNotFoundException(id + " id'sine sahip bir kahve sistemde mevcut değil"));

            coffeRepository.deleteById(id);

        } catch (AppValidationException | AppNotFoundException | AppException e) {
            throw e;
        } catch (Exception e) {
            throw new AppException(e.getMessage(), e);

        }

    }

    @Override
    public CoffeOrderResponseDto coffeOrder(Long id) {

        try {
            validateId(id);
            CoffeDto coffeDto = getById(id);
            List<CoffeeOrderItemDto> items = mapIngredientsToOrderItems(coffeDto.getIngredients());

            return buildCoffeeOrderResponse(coffeDto, items);
        } catch (AppValidationException | AppNotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RuntimeException("Kahve siparişi alınırken beklenmeyen bir hata oluştu", ex);
        }

    }

    private void validateId(Long id) {
        if (id == null) {
            throw new AppValidationException("ID bilgisi boş olamaz");
        }
    }

    private List<CoffeeOrderItemDto> mapIngredientsToOrderItems(List<Ingredient> ingredients) {
        if (ingredients == null || ingredients.isEmpty()) {
            return List.of();
        }

        return ingredients.stream().map(ingredient -> {

            try {
                AvailableProductDto product = availableProductService.getById(ingredient.getProductId());
                return CoffeeOrderItemDto.builder()
                        .productName(product.getProductName())
                        .amount(ingredient.getAmount())
                        .build();
            }catch (AppException e){
                throw new AppNotFoundException("Ürün Bulunamadı");
            }
        }).toList();
    }

    private CoffeOrderResponseDto buildCoffeeOrderResponse(CoffeDto coffeDto, List<CoffeeOrderItemDto> items) {
        return CoffeOrderResponseDto.builder()
                .coffeeName(coffeDto.getCoffeeName())
                .price(coffeDto.getPrice())
                .preparationTime(coffeDto.getPreparationTime())
                .calorie(coffeDto.getCalorie())
                .items(items)
                .build();
    }

}
