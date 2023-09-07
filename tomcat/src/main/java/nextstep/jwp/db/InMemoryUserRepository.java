package nextstep.jwp.db;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import nextstep.jwp.model.User;

public class InMemoryUserRepository {

    private static final Map<String, User> database = new ConcurrentHashMap<>();
    private static final AtomicInteger count = new AtomicInteger(1);

    static {
        final User user = new User(1L, "gugu", "password", "hkkang@woowahan.com");
        database.put(user.getAccount(), user);
    }

    public static void save(User user) {
        final User savedUser = new User((long) count.incrementAndGet(), user);
        database.put(savedUser.getAccount(), savedUser);
    }

    public static Optional<User> findByAccount(String account) {
        return Optional.ofNullable(database.get(account));
    }

    public static Optional<User> findByAccountAndPassword(String account, String password) {
        final Optional<User> optionalUser = Optional.ofNullable(database.get(account));

        if (optionalUser.isPresent()) {
            final User user = optionalUser.get();
            if (user.checkPassword(password)) {
                return optionalUser;
            }
        }

        return Optional.empty();
    }

    private InMemoryUserRepository() {
    }
}
