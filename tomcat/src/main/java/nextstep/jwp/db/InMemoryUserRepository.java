package nextstep.jwp.db;

import nextstep.jwp.model.User;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryUserRepository {

    private static final Map<String, User> database = new ConcurrentHashMap<>();
    private static final int INCREMENT_BY_ONE = 1;

    static {
        final User user = new User(1L, "gugu", "password", "hkkang@woowahan.com");
        database.put(user.getAccount(), user);
    }

    public static void save(User user) {
        final int id = database.size() + INCREMENT_BY_ONE;
        final User savedUser = new User(
                Integer.toUnsignedLong(id),
                user.getAccount(),
                user.getPassword(),
                user.getEmail()
        );

        database.put(savedUser.getAccount(), savedUser);
    }

    public static Optional<User> findByAccount(String account) {
        return Optional.ofNullable(database.get(account));
    }

    private InMemoryUserRepository() {
    }
}
