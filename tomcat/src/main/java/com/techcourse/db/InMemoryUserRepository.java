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

    public static Optional<User> findByAccount(String account) {
        return Optional.ofNullable(database.get(account));
    }

    public static User getByAccountAndPassword(String account, String password) {
        User user = database.get(account);
        if (user == null || !user.checkPassword(password)) {
            throw new IllegalArgumentException("로그인 혹은 비밀번호가 틀렸습니다.");
        }
        return user;
    }

    private InMemoryUserRepository() {}
}
