package nextstep.jwp.database;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import nextstep.jwp.model.User;

public class InMemoryUserRepository {

    private static final Map<Long, User> database = new ConcurrentHashMap<>();

    private static AtomicLong counter = new AtomicLong();

    static {
        final User user = new User("gugu", "password", "hkkang@woowahan.com");
        database.put(counter.getAndIncrement(), user);
    }

    private InMemoryUserRepository() {
    }

    public static void save(User user) {
        database.put(counter.getAndIncrement(), user);
    }

    public static Long getIdByCredentials(String account, String password) {
        return database.entrySet().stream()
                .filter(entry -> entry.getValue().hasSameCredential(account, password))
                .findFirst()
                .orElseThrow()
                .getKey();
    }

    public static boolean hasSameCredential(String account, String password) {
        return database.values().stream()
                .anyMatch(user -> user.hasSameCredential(account, password));
    }
}
