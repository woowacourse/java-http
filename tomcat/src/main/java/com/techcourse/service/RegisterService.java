package com.techcourse.service;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;

public class RegisterService {

    private static final RegisterService INSTANCE = new RegisterService();

    private RegisterService() {
    }

    public static RegisterService getInstance() {
        return INSTANCE;
    }

    public void register(String account, String password, String email) {
        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
    }
}
