package nextstep.jwp.application.db;


import nextstep.jwp.application.model.User;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryUserRepository {

    private static final Map<String, User> database = new ConcurrentHashMap<>();
    private static final AtomicLong atomicLong = new AtomicLong();

    static {
        User gugu = new User(atomicLong.incrementAndGet(), "gugu", "password", "hkkang@woowahan.com");
        save(gugu);
    }

    public static void save(User user) {
        database.put(
                user.getAccount(),
                new User(atomicLong.incrementAndGet(), user.getAccount(), user.getPassword(), user.getEmail())
        );
    }

    public static Optional<User> findByAccount(String account) {
        return Optional.ofNullable(database.get(account));
    }
}
