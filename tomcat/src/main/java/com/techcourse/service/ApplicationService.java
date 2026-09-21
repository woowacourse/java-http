package com.techcourse.service;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.DuplicateAccountException;
import com.techcourse.model.User;
import java.util.Optional;

public class ApplicationService {

    public Optional<User> login(String account, String password) {
        if (account == null || password == null) {
            return Optional.empty();
        }

        return InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password));
    }

    public User register(String account, String password, String email) {
        if(account == null || password == null || email == null) {
            throw new IllegalArgumentException();
        }

        if(InMemoryUserRepository.findByAccount(account).isPresent()) {
            throw new DuplicateAccountException();
        }

        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
        return user;
    }
}
