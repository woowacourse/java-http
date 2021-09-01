package nextstep.jwp.service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import nextstep.jwp.model.User;

public class MockInMemoryRepository {
    private static final Map<String, User> database = new ConcurrentHashMap<>();

    public static void save(User user) {
        database.put(user.getAccount(), user);
    }

    public static Optional<User> findByAccount(String account) {
        return Optional.ofNullable(database.get(account));
    }

    public static int size() {
        return database.size();
    }
}
