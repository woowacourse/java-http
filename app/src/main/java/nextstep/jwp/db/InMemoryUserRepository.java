package nextstep.jwp.db;


import nextstep.jwp.model.User;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryUserRepository {

    private static final Map<String, User> database = new ConcurrentHashMap<>();
    private static long id = 0;

    static {
        final User user = new User("gugu", "password", "hkkang@woowahan.com");
        user.setId(++id);
        database.put(user.getAccount(), user);
    }

    public static void save(User user) {
        user.setId(++id);
        database.put(user.getAccount(), user);
    }

    public static Optional<User> findByAccount(String account) {
        return Optional.ofNullable(database.get(account));
    }
}
