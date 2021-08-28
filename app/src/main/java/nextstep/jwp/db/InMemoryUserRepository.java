package nextstep.jwp.db;


import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import nextstep.jwp.model.User;

public class InMemoryUserRepository {

    private static final Map<String, User> database = new ConcurrentHashMap<>();
    public static int autoIncrementId = 0;
    static {
        final User user = new User(1, "gugu", "password", "hkkang@woowahan.com");
        database.put(user.getAccount(), user);
        autoIncrementId += 2;
    }

    public static void save(User user) {
        database.put(user.getAccount(), user);
    }

    public static Optional<User> findByAccount(String account) {
        return Optional.ofNullable(database.get(account));
    }

    public static Optional<User> findByAccountAndPassword(String account, String password) {
        Optional<User> user = Optional.ofNullable(database.get(account));
        if (user != null && user.get().checkPassword(password)) {
            return user;
        }
        return Optional.empty();
    }
}
