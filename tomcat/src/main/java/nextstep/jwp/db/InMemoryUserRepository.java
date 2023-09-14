package nextstep.jwp.db;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import nextstep.jwp.model.User;

public class InMemoryUserRepository {

    private static AtomicLong RESERVED_ID = new AtomicLong(0);
    private static final Map<String, User> database = new ConcurrentHashMap<>();

    static {
        save(new User("gugu", "password", "hkkang@woowahan.com"));
    }

    public static void save(User user) {
        user.setId(RESERVED_ID.incrementAndGet());
        database.put(user.getAccount(), user);
    }

    public static Optional<User> findByAccount(String account) {
        return Optional.ofNullable(database.get(account));
    }

    public static List<User> findAll() {
        return new ArrayList<>(database.values());
    }

    private InMemoryUserRepository() {
    }
}
