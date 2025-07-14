package com.example.coffeApp.controller;

import com.example.coffeApp.config.TokenInfo;
import com.example.coffeApp.dto.auth.AuthenticationRequest;
import com.example.coffeApp.dto.payload.JwtResponse;
import com.example.coffeApp.dto.user.UserAddDto;
import com.example.coffeApp.dto.user.UserResultDto;
import com.example.coffeApp.entity.UserEntity;
import com.example.coffeApp.exception.AppValidationException;
import com.example.coffeApp.repository.UserRepository;
import com.example.coffeApp.service.UserService;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;


    @PostMapping("/add")
    public UserResultDto addUser(@RequestBody UserAddDto userAddDto) {
        return userService.userAdd(userAddDto);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<?> loginUser(@RequestBody AuthenticationRequest authenticationRequest) {

        Optional<UserEntity> kullanici = userRepository.findByEmail(authenticationRequest.getUsername());

        if (!kullanici.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Kullanıcı adı alanı hatalı");
        }

        if (!BCrypt.checkpw(authenticationRequest.getPassword(), kullanici.get().getPassword())) {
            throw new AppValidationException("Parola hatalı. Tekrar deneyin.");
        }

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword()));
        User user = (User) authentication.getPrincipal();
        Key key = new SecretKeySpec(Base64.getDecoder().decode(TokenInfo.SECRET),
                SignatureAlgorithm.HS256.getJcaName());

        long currentMillis = System.currentTimeMillis();
        Date now = new Date(currentMillis);
        JwtBuilder builder = Jwts.builder()
                .setIssuedAt(now)
                .setSubject(user.getUsername())
                .setIssuer(TokenInfo.ISSUER)
                .setExpiration(new Date(System.currentTimeMillis() + TokenInfo.EXPIRATION_TIME))
                .signWith(key);

        return ResponseEntity.ok(new JwtResponse(builder.compact(), user.getUsername(), grantedAuthoritiesConvertString(user.getAuthorities()), kullanici.get().getId()));

    }

    @RequestMapping("/admin")
    public String loginAdmin() {
        return "Admin kullanıcı girişi başarılı";
    }

    @GetMapping
    public ResponseEntity<List<UserResultDto>> getAll() {
        return ResponseEntity.ok(userService.getAllUer());
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }


    private static List<String> grantedAuthoritiesConvertString(Collection<? extends GrantedAuthority> grantedAuthorities) {
        List<String> role = new ArrayList<>();
        for (GrantedAuthority grantedAuthority : grantedAuthorities) {
            role.add(grantedAuthority.getAuthority());
        }
        return role;
    }
}
