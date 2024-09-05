package com.techcourse.db;

import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.util.Map;
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

    public static boolean exists(String account, String password) {
        return database.containsKey(account)
               && database.get(account).checkPassword(password);
    }

    public static User getByAccount(String account) {
        if (database.containsKey(account)) {
            return database.get(account);
        }
        throw new UncheckedServletException("유저가 존재하지 않습니다.");
    }

    public static boolean existsByAccount(String account) {
        return database.containsKey(account);
    }

    private InMemoryUserRepository() {
    }
}
