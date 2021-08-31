package nextstep.jwp.db;


import nextstep.jwp.model.User;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryUserRepository {

    private InMemoryUserRepository() {

    }

    private static final Map<String, User> database = new ConcurrentHashMap<>();
    private static int index = 1;

    static {
        final User user = new User(1, "gugu", "password", "hkkang@woowahan.com");
        save(user);
    }

    public static void save(User user) {
        database.put(user.getAccount(), user);
        index++;
    }

    public static Optional<User> findByAccount(String account) {
        return Optional.ofNullable(database.get(account));
    }

    public static int nextId() {
        return index;
    }
}
