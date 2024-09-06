package com.techcourse.service;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import com.techcourse.request.RegisterRequest;

public class RegisterService {


    public boolean register(RegisterRequest request) {
        if (InMemoryUserRepository.notExistByAccount(request.account())) {
            User user = new User(request);
            InMemoryUserRepository.save(user);
            return true;
        }
        return false;
    }
}
