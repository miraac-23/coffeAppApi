package com.example.coffeApp.dto.payload;

import java.util.List;

public class JwtResponse {
    private String token;
    private String username;
    private Integer userId;
    private List<String> roles;

    public JwtResponse(String accessToken, String username, List<String> roles, Integer userId) {
        this.token = accessToken;

        this.username = username;

        this.roles = roles;

        this.userId = userId;
    }

    public String getAccessToken() {
        return token;
    }

    public void setAccessToken(String accessToken) {
        this.token = accessToken;
    }

    public String getUsername() {
        return username;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getRoles() {
        return roles;
    }
}
