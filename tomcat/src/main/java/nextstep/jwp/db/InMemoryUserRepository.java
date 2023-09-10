package nextstep.jwp.db;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import nextstep.jwp.model.User;

public class InMemoryUserRepository {

    private static final Map<String, User> database = new ConcurrentHashMap<>();
    private static final AtomicLong sequence = new AtomicLong(1);

    static {
        final var user = new User(sequence.getAndIncrement(), "gugu", "password", "hkkang@woowahan.com");
        database.put(user.getAccount(), user);
    }

    private InMemoryUserRepository() {
    }

    public static void save(User user) {
        final var newUser = new User(sequence.getAndIncrement(), user.getAccount(), user.getPassword(), user.getEmail());
        database.put(user.getAccount(), newUser);
    }

    public static Optional<User> findByAccount(String account) {
        return Optional.ofNullable(database.get(account));
    }
}
