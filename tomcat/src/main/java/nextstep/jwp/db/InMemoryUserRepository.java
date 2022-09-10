package nextstep.jwp.db;

import nextstep.jwp.model.User;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryUserRepository {

    private static final Map<String, User> database = new ConcurrentHashMap<>();

    static {
        final User user = new User(1L, "gugu", "password", "hkkang@woowahan.com");
        database.put(user.getAccount(), user);
    }

    public static void save(User user) {
        database.put(user.getAccount(), user);
    }

    public static Optional<User> findByAccount(String account) {
        return Optional.ofNullable(database.get(account));
    }

    public static boolean existsByAccount(String account) {
        return database.containsKey(account);
    }

    private InMemoryUserRepository() {}

    public static boolean isValidUser(User user) {
        String account = user.getAccount();
        if (!existsByAccount(account)) {
            return false;
        }

        User dbUser = database.get(account);
        return dbUser.equals(user);
    }
}
