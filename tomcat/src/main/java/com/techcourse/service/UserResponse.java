package com.techcourse.service;

import com.techcourse.model.User;

public record UserResponse(
        String account,
        String email
) {
    public static UserResponse from(User user) {
        return new UserResponse(user.getAccount(), user.getEmail());
    }
}
