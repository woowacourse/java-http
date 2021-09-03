package nextstep.jwp.db;


import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import nextstep.jwp.model.User;

public class InMemoryUserRepository {

    private static final Map<String, User> database = new ConcurrentHashMap<>();

    private static Long seq = 1L;

    static {
        final User user = new User(1, "gugu", "password", "hkkang@woowahan.com");
        database.put(user.getAccount(), user);
    }

    private InMemoryUserRepository() {
    }

    public static void save(User user) {
        database.put(user.getAccount(), user);
    }

    public static boolean exist(User user) {
        return findByUser(user).isPresent();
    }

    private static Optional<User> findByUser(User user) {
        return findByAccount(user.getAccount());
    }

    public static Optional<User> findByAccount(String account) {
        return Optional.ofNullable(database.get(account));
    }

    public static Long getNextId() {
        return ++seq;
    }
}
