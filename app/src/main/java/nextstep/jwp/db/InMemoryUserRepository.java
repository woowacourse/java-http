package nextstep.jwp.db;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import nextstep.jwp.model.User;

public class InMemoryUserRepository {

    private static final Map<String, User> database = new ConcurrentHashMap<>();
    private static Long nextId = 2L;

    static {
        final User user = new User(1L, "gugu", "password", "hkkang@woowahan.com");
        database.put(user.getAccount(), user);
    }

    public static void save(User user) {
        if (existsByAccount(user.getAccount())) {
            throw new IllegalArgumentException("This user already exists");
        }

        database.put(user.getAccount(), new User(nextId++, user));
    }

    public static boolean existsByAccount(final String account) {
        return database.containsKey(account);
    }

    public static boolean existsByAccountAndPassword(final String account, final String password) {
        return existsByAccount(account) && findByAccount(account).checkPassword(password);
    }

    public static User findByAccount(String account) {
        if (!existsByAccount(account)) {
            throw new IllegalArgumentException(String.format("Cannot find account (%s).", account));
        }
        return database.get(account);
    }
}
