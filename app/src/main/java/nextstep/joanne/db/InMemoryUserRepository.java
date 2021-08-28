package nextstep.joanne.db;


import nextstep.joanne.model.User;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryUserRepository {

    private InMemoryUserRepository() {
    }

    private static final Map<String, User> database = new ConcurrentHashMap<>();

    static {
        final User user = new User(1, "gugu", "password", "hkkang@woowahan.com");
        database.put(user.getAccount(), user);
    }

    public static void save(User user) {
        if (user.getId() == 0) {
            user.setId(nextId());
        }
        database.put(user.getAccount(), user);
    }

    public static Optional<User> findByAccount(String account) {
        return Optional.ofNullable(database.get(account));
    }

    public static long nextId() {
        return new ArrayList<>(database.values())
                .get(database.size() - 1)
                .getId() + 1;
    }
}
