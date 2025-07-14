package com.example.coffeApp.controller;

import com.example.coffeApp.dto.auth.AuthenticationRequest;
import com.example.coffeApp.dto.payload.JwtResponse;
import com.example.coffeApp.dto.user.UserAddDto;
import com.example.coffeApp.dto.user.UserResultDto;
import com.example.coffeApp.entity.UserEntity;
import com.example.coffeApp.exception.AppValidationException;
import com.example.coffeApp.repository.UserRepository;
import com.example.coffeApp.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.*;
import org.springframework.security.core.userdetails.User;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    private UserAddDto userAddDto;
    private UserResultDto userResultDto;
    private UserEntity userEntity;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        userAddDto = new UserAddDto();
        userAddDto.setEmail("test@example.com");
        userAddDto.setPassword("123456");

        userResultDto = new UserResultDto();
        userResultDto.setId(1);
        userResultDto.setEmail("test@example.com");

        userEntity = new UserEntity();
        userEntity.setId(1);
        userEntity.setEmail("test@example.com");
        userEntity.setPassword(org.mindrot.jbcrypt.BCrypt.hashpw("123456", org.mindrot.jbcrypt.BCrypt.gensalt()));
    }


    @Test
    @DisplayName("addUser - başarıyla kullanıcı eklemeli")
    void testAddUserSuccess() {
        when(userService.userAdd(userAddDto)).thenReturn(userResultDto);

        UserResultDto result = userController.addUser(userAddDto);

        assertEquals("test@example.com", result.getEmail());
        verify(userService).userAdd(userAddDto);
    }


    @Test
    @DisplayName("getAll - kullanıcı listesi dönmeli")
    void testGetAllSuccess() {
        List<UserResultDto> resultList = List.of(userResultDto);

        when(userService.getAllUer()).thenReturn(resultList);

        ResponseEntity<List<UserResultDto>> response = userController.getAll();

        assertEquals(1, response.getBody().size());
        assertEquals("test@example.com", response.getBody().get(0).getEmail());
    }


    @Test
    @DisplayName("delete - geçerli id ile başarılı silme")
    void testDeleteSuccess() {
        ResponseEntity<Void> response = userController.delete(1);

        assertEquals(204, response.getStatusCodeValue());
        verify(userService).delete(1);
    }


    @Test
    @DisplayName("loginUser - kullanıcı yoksa 404 dönmeli")
    void testLoginUserUserNotFound() {
        AuthenticationRequest req = new AuthenticationRequest();
        req.setUsername("notfound@example.com");
        req.setPassword("pass");
        when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        ResponseEntity<?> response = userController.loginUser(req);

        assertEquals(404, response.getStatusCodeValue());
        assertEquals("Kullanıcı adı alanı hatalı", response.getBody());
    }

    @Test
    @DisplayName("loginUser - parola hatalıysa AppValidationException fırlatılmalı")
    void testLoginUserWrongPassword() {
        AuthenticationRequest req = new AuthenticationRequest();
        req.setUsername("test@example.com"); // düzeltildi
        req.setPassword("pass");

        userEntity.setPassword(org.mindrot.jbcrypt.BCrypt.hashpw("correctpass", org.mindrot.jbcrypt.BCrypt.gensalt()));
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(userEntity));

        assertThrows(AppValidationException.class, () -> userController.loginUser(req));
    }


    @Test
    @DisplayName("loginUser - geçerli bilgilerle JWT dönmeli")
    void testLoginUserSuccess() {
        AuthenticationRequest req = new AuthenticationRequest();
        req.setUsername("admin@admin.com");
        req.setPassword("123123");

        userEntity.setEmail("admin@admin.com");
        userEntity.setId(1);
        userEntity.setPassword(org.mindrot.jbcrypt.BCrypt.hashpw("123123", org.mindrot.jbcrypt.BCrypt.gensalt()));

        when(userRepository.findByEmail("admin@admin.com")).thenReturn(Optional.of(userEntity));

        Collection<GrantedAuthority> authorities = List.of((GrantedAuthority) () -> "ROLE_USER");
        User springUser = new User("admin@admin.com", "123123", authorities);

        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(springUser);

        when(authenticationManager.authenticate(any())).thenReturn(auth);

        ResponseEntity<?> response = userController.loginUser(req);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof JwtResponse);

        JwtResponse jwt = (JwtResponse) response.getBody();
        assertEquals("admin@admin.com", jwt.getUsername());
        assertEquals(List.of("ROLE_USER"), jwt.getRoles());
        assertEquals(1, jwt.getUserId());
    }


    @Test
    @DisplayName("loginAdmin - başarılı mesaj dönmeli")
    void testLoginAdminSuccess() {
        String result = userController.loginAdmin();

        assertEquals("Admin kullanıcı girişi başarılı", result);
    }
}
