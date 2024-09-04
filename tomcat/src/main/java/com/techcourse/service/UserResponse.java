package com.techcourse.service;

import com.techcourse.model.User;

public record UserResponse(
        Long id,
        String account,
        String email
) {
    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getAccount(), user.getEmail());
    }
}
