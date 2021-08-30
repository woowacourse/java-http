package nextstep.jwp.application.db;


import nextstep.jwp.application.model.User;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryUserRepository {

    private static final Map<String, User> database = new ConcurrentHashMap<>();
    private static long autoIncrementValue = 1;

    static {
        final User user = new User("gugu", "password", "hkkang@woowahan.com");
        save(user);
    }

    public static void save(User user) {
        user.setId(autoIncrementValue);
        database.put(user.getAccount(), user);
        autoIncrementValue++;
    }

    public static Optional<User> findByAccount(String account) {
        return Optional.ofNullable(database.get(account));
    }
}
