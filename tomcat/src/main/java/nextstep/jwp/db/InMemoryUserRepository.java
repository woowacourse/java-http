package nextstep.jwp.db;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import nextstep.jwp.model.User;

public class InMemoryUserRepository {

    private static final Map<String, User> DATABASE = new ConcurrentHashMap<>();
    private static final AtomicLong SEQUENCE = new AtomicLong(1);

    static {
        final var user = new User(SEQUENCE.getAndIncrement(), "gugu", "password", "hkkang@woowahan.com");
        DATABASE.put(user.getAccount(), user);
    }

    private InMemoryUserRepository() {
    }

    public static void save(User user) {
        final var newUser = new User(SEQUENCE.getAndIncrement(), user.getAccount(), user.getPassword(), user.getEmail());
        DATABASE.put(user.getAccount(), newUser);
    }

    public static Optional<User> findByAccount(String account) {
        return Optional.ofNullable(DATABASE.get(account));
    }
}
