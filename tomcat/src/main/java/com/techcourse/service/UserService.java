package com.techcourse.service;

import java.util.Map;
import java.util.Optional;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;

public class UserService {
    private static final String ACCOUNT_FIELD = "account";
    private static final String PASSWORD_FIELD = "password";
    private static final String EMAIL_FIELD = "email";

    public User authenticateUser(Map<String, String> userInformation) {
        if (isWrongInformation(userInformation)) {
            return null;
        }

        String account = userInformation.get(ACCOUNT_FIELD);
        String password = userInformation.get(PASSWORD_FIELD);

        return Optional.ofNullable(InMemoryUserRepository.findByAccount(account))
                .filter(user -> user.checkPassword(password))
                .orElse(null);
    }

    public void saveUser(Map<String, String> userInformation) {
        if (isWrongInformation(userInformation)) {
            return;
        }
        String account = userInformation.get(ACCOUNT_FIELD);
        String password = userInformation.get(PASSWORD_FIELD);
        String email = userInformation.getOrDefault(EMAIL_FIELD, "");

        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
    }

    private boolean isWrongInformation(Map<String, String> userInformation) {
        if (!userInformation.containsKey(ACCOUNT_FIELD)) {
            return true;
        }
        return !userInformation.containsKey(PASSWORD_FIELD);
    }
}
