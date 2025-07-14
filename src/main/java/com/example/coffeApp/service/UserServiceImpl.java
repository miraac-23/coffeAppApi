package com.example.coffeApp.service;


import com.example.coffeApp.dto.user.UserAddDto;
import com.example.coffeApp.dto.user.UserResultDto;
import com.example.coffeApp.entity.UserEntity;
import com.example.coffeApp.exception.AppException;
import com.example.coffeApp.exception.AppNotFoundException;
import com.example.coffeApp.exception.AppValidationException;
import com.example.coffeApp.mapper.UserMapper;
import com.example.coffeApp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    private UserMapper userMapper;

    @Override
    public UserResultDto getUserById(Integer id) {
        if (id == null) {
            throw new AppValidationException("Id bilgisi boş olamaz");
        }
        try {
            UserEntity userEntity = userRepository.findById(id)
                    .orElseThrow(() -> new AppNotFoundException(id + " id'sine sahip bir kullanıcı sistemde mevcut değil"));

            return userMapper.entityToDto(userEntity);

        } catch (AppValidationException | AppNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new AppException(e.getMessage(), e);
        }
    }

    @Override
    public UserResultDto userAdd(UserAddDto userAddDto) {
        if (userAddDto == null) {
            throw new AppValidationException("eksik bilgi");
        }

        Optional<UserEntity> user = userRepository.findByEmail(userAddDto.getEmail());
        if (user.isPresent()) {
            throw new AppValidationException("Aynı mail adresine kayıtlı bir kullanıcı sistemde bulunmaktadır, " +
                    "farklı bir kullanıcı adı ile tekrar deneyiniz");
        }

        try {
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String hashedPassword = passwordEncoder.encode(userAddDto.getPassword());
            userAddDto.setPassword(hashedPassword);

            UserEntity userEntity = userMapper.addDtoToEntity(userAddDto);

            userRepository.save(userEntity);

            return userMapper.entityToDto(userEntity);

        } catch (AppException | AppValidationException e) {
            throw e;
        } catch (Exception e) {
            throw new AppException(e.getMessage(), e);
        }
    }


    @Override
    public UserResultDto getUserByEmail(String email) {
        if (email == null) {
            throw new AppValidationException("Id bilgisi boş olamaz");
        }
        try {
            UserEntity user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new AppNotFoundException(email + " e sahip bir kullanıcı sistemde mevcut değil"));
            return userMapper.entityToDto(user);
        } catch (AppValidationException | AppNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new AppException(e.getMessage(), e);
        }
    }

    @Override
    public List<UserResultDto> getAllUer() {
        List<UserResultDto> userResultDtoList = userRepository.findAll().stream()
                .map(userMapper::entityToDto)
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(userResultDtoList)) {
            throw new AppNotFoundException("Veri tabanında kullanıcı kaydına rastlanılmamıştır");
        }

        return userResultDtoList;
    }

    @Override
    public void delete(Integer id) {
        try {
            validateId(id);
            userRepository.findById(id)
                    .orElseThrow(() -> new AppNotFoundException(id + " id'sine sahip bir kullanıcı sistemde mevcut değil"));

            userRepository.deleteById(id);

        } catch (AppValidationException | AppNotFoundException | AppException e) {
            throw e;
        } catch (Exception e) {
            throw new AppException(e.getMessage(), e);

        }
    }

    private void validateId(Integer id) {
        if (id == null) {
            throw new AppValidationException("ID bilgisi boş olamaz");
        }
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setUserMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }
}
