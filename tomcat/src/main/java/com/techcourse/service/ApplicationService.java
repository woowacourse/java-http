package com.techcourse.service;

import com.techcourse.db.InMemoryUserRepository;
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

    public void register(String account, String password, String email) {
        if(account == null || password == null || email == null) {
            throw new IllegalArgumentException();
        }

        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
    }

    public boolean isUser(User user) {
        return InMemoryUserRepository.findByAccount(user.getAccount()).isPresent();
    }
}
