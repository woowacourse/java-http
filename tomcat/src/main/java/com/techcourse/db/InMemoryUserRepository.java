package com.techcourse.db;

import com.techcourse.model.User;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryUserRepository {

    private static final Map<String, User> database = new ConcurrentHashMap<>();

    static {
        final User user = new User(1L, "gugu", "password", "hkkang@woowahan.com");
        database.put(user.getAccount(), user);
    }

    public static void save(User user) {
        database.put(user.getAccount(), user);
    }

    public static User findByAccountAndPassword(String account, String password) {
        Optional<User> user = Optional.ofNullable(database.get(account));

        User findUser = user.orElseThrow(() -> new IllegalArgumentException("인증에 실패하였습니다."));
        if (findUser.checkPassword(password)) {
            return findUser;
        }
        throw new IllegalArgumentException("인증에 실패하였습니다.");
    }

    private InMemoryUserRepository() {
    }
}
