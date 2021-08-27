package nextstep.jwp.db;


import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import nextstep.jwp.model.User;

public class InMemoryUserRepository {

    private static final Map<String, User> database = new ConcurrentHashMap<>();
    private static final AtomicInteger atomicInteger = new AtomicInteger();

    static {
        final User user = new User(getNextCountValue(), "gugu", "password", "hkkang@woowahan.com");
        database.put(user.getAccount(), user);
    }

    public static void save(User user) {
        if (user.isEmptyId()) {
            user.insertId(getNextCountValue());
        }
        database.put(user.getAccount(), user);
    }

    public static Optional<User> findByAccount(String account) {
        return Optional.ofNullable(database.get(account));
    }

    public static int getNextCountValue() {
        return atomicInteger.incrementAndGet();
    }
}
