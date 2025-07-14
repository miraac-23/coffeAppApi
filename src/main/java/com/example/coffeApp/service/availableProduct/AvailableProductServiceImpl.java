package com.example.coffeApp.service.availableProduct;

import com.example.coffeApp.dto.availableProduct.AvailableProductCreateDto;
import com.example.coffeApp.dto.availableProduct.AvailableProductDto;
import com.example.coffeApp.entity.AvailableProductEntity;
import com.example.coffeApp.exception.AppException;
import com.example.coffeApp.exception.AppNotFoundException;
import com.example.coffeApp.exception.AppValidationException;
import com.example.coffeApp.mapper.AvailableProductMapper;
import com.example.coffeApp.repository.AvailableProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AvailableProductServiceImpl implements AvailableProductService {

    private final AvailableProductRepository availableProductRepository;
    private final AvailableProductMapper availableProductMapper;

    @Override
    public AvailableProductDto create(AvailableProductCreateDto availableProductCreateDto) {

        try {
            if (availableProductCreateDto == null) {
                throw new AppValidationException("Ürün bilgileri boş olamaz.");
            }

            Optional<AvailableProductEntity> optionalAvailableProductEntity = availableProductRepository.findByProductName(availableProductCreateDto.getProductName());

            if (optionalAvailableProductEntity.isPresent()) {
                throw new AppValidationException("Aynı isimde ürün eklemesi yapılamamaktadır.");
            }

            AvailableProductEntity availableProductEntity = availableProductMapper.createDtoToEntity(availableProductCreateDto);
            availableProductRepository.save(availableProductEntity);

            return availableProductMapper.entityToDto(availableProductEntity);

        } catch (AppException | AppValidationException e) {
            throw e;
        } catch (Exception e) {
            throw new AppException(e.getMessage(), e);
        }

    }

    @Override
    public AvailableProductDto update(AvailableProductDto availableProductDto) {

        try {
            if (availableProductDto == null) {
                throw new AppValidationException("ürün bilgileri boş olamaz");
            }

            AvailableProductEntity availableProductEntity = availableProductRepository.findById(availableProductDto.getId())
                    .orElseThrow(() -> new AppNotFoundException("Ürün bulunamadı"));

            availableProductEntity.setProductName(availableProductDto.getProductName());

            availableProductRepository.save(availableProductEntity);
            return availableProductMapper.entityToDto(availableProductEntity
            );
        } catch (AppValidationException | AppNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new AppException(e.getMessage(), e);
        }

    }


    @Override
    public void delete(Long id) {

        try {
            if (id == null) {
                throw new AppValidationException("Ürün silmek için id bilgisi eksik");
            }
            availableProductRepository.findById(id)
                    .orElseThrow(() -> new AppNotFoundException(id + " id'sine sahip bir ürün sistemde mevcut değil"));


            availableProductRepository.deleteById(id);

        } catch (AppValidationException | AppNotFoundException | AppException e) {
            throw e;
        } catch (Exception e) {
            throw new AppException(e.getMessage(), e);

        }

    }

    @Override
    public AvailableProductDto getById(Long id) {
        try {
            if (id == null) {
                throw new AppValidationException("Id bilgisi boş olamaz");
            }
            AvailableProductEntity availableProductEntity = availableProductRepository.findById(id)
                    .orElseThrow(() -> new AppNotFoundException(id + " id'sine sahip bir ürün sistemde mevcut değil"));

            return availableProductMapper.entityToDto(availableProductEntity);
        } catch (AppValidationException | AppNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new AppException(e.getMessage(), e);
        }
    }

    @Override
    public List<AvailableProductDto> getAll() {
        try {
            List<AvailableProductDto> availableProductDtoList = availableProductRepository.findAll().stream()
                    .map(availableProductMapper::entityToDto)
                    .collect(Collectors.toList());

            if (CollectionUtils.isEmpty(availableProductDtoList)) {
                throw new AppNotFoundException("Veri tabanında ürün kaydına rastlanılmamıştır");
            }

            return availableProductDtoList;
        } catch (AppNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new AppException(e.getMessage(), e);
        }
    }


}
