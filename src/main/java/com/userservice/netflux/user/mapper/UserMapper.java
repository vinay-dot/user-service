package com.userservice.netflux.user.mapper;

import com.userservice.netflux.user.dto.RegisterRequest;
import com.userservice.netflux.user.entity.User;

public class UserMapper {

    private UserMapper() {
    }

    public static User toEntity(RegisterRequest request, String hashedPassword) {
        var user = new User();
        user.setUsername(request.username());
        user.setPassword(hashedPassword);
        user.setName(request.name());
        return user;
    }
}
