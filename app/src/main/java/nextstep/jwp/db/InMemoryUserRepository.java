package nextstep.jwp.db;


import nextstep.jwp.model.User;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryUserRepository {

    private static final Map<String, User> database = new ConcurrentHashMap<>();
    private static Long nextId = 1L;

    static {
        final User user = new User("gugu", "password", "hkkang@woowahan.com");
        save(user);
    }

    public static User save(User user) {
        User persistedUser = new User(nextId++, user.getAccount(), user.getPassword(), user.getEmail());
        database.put(persistedUser.getAccount(), persistedUser);

        return persistedUser;
    }

    public static Optional<User> findByAccount(String account) {
        return Optional.ofNullable(database.get(account));
    }
}
