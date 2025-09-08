package com.techcourse.service;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Optional;

public class UserService {

    public Optional<User> login(final String account, final String password) {
        Optional<User> user = InMemoryUserRepository.findByAccount(account);
        if (user.isEmpty()) {
            return Optional.empty();
        }

        final User existingUser = user.get();
        if (!existingUser.checkPassword(password)) {
            return Optional.empty();
        }

        return Optional.of(existingUser);
    }

    public boolean signup(final String account, final String password, final String email) {
        final User createdUser = new User(account, password, email);
        InMemoryUserRepository.save(createdUser);
        return true;
    }
}
