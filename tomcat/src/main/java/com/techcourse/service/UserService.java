package com.techcourse.service;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Optional;

public class UserService {

    public boolean login(final String account, final String password) {
        Optional<User> user = InMemoryUserRepository.findByAccount(account);
        if (user.isEmpty()) {
            return false;
        }

        final User existingUser = user.get();
        return existingUser.checkPassword(password);
    }

    public boolean signup(final String account, final String password, final String email) {
        final User createdUser = new User(account, password, email);
        InMemoryUserRepository.save(createdUser);
        return true;
    }
}
