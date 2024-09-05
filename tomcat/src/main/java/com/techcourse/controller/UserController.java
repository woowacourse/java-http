package com.techcourse.controller;

import com.techcourse.controller.dto.Response;
import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.util.Map;

public class UserController {

    public Response<User> searchUserData(Map<String, String> params) {
        String account = params.getOrDefault("account", "");
        String password = params.getOrDefault("password", "");

        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new UncheckedServletException(new IllegalArgumentException("일치하는 계정이 존재하지 않습니다.")));

        if (!user.checkPassword(password)) {
            throw new UncheckedServletException(new IllegalArgumentException("비밀번호가 일치하지 않습니다."));
        }

        return new Response<>(user);
    }
}
