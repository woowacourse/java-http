package nextstep.jwp.db;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import nextstep.jwp.model.User;

public class InMemoryUserRepository {

    private static final Map<String, User> database = new ConcurrentHashMap<>();
    private static Long publishedId = 1L;

    static {
        final User user = new User(1L, "gugu", "password", "hkkang@woowahan.com");
        database.put(user.getAccount(), user);
    }

    private InMemoryUserRepository() {
    }

    public static Optional<User> findByAccount(String account) {
        return Optional.ofNullable(database.get(account));
    }

    public static void save(User user) {
        User saveUser = new User(++publishedId, user.getAccount(), user.getPassword(), user.getEmail());
        database.put(saveUser.getAccount(), saveUser);
    }
}
