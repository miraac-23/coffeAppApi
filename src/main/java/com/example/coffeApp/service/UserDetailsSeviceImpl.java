package com.example.coffeApp.service;

import com.example.coffeApp.entity.UserEntity;
import com.example.coffeApp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class UserDetailsSeviceImpl implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserEntity> user = userRepository.findByEmail(username);
        Set<GrantedAuthority> grantedAuthoritySet = new HashSet<>();
        if (user.isPresent()) {
            grantedAuthoritySet.add(new SimpleGrantedAuthority("ROLE_" + user.get().getUserType()));
        } else {
            throw new UsernameNotFoundException(username);
        }
        return new User(username, user.get().getPassword(), grantedAuthoritySet);
    }




}
