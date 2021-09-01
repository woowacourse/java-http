package nextstep.jwp.db;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import nextstep.jwp.exception.UsernameConflictException;
import nextstep.jwp.model.User;

public class InMemoryUserRepository {

    private static final Map<String, User> database = new ConcurrentHashMap<>();
    private static final int DEFAULT_USER_ID = 0;
    private static long id = 0L;

    static {
        final User user = new User(++id, "gugu", "password", "hkkang@woowahan.com");
        database.put(user.getAccount(), user);
    }

    private InMemoryUserRepository() {
    }

    public static void save(User user) {
        userIdValidate(user);
        if (database.containsKey(user.getAccount())) {
            throw new UsernameConflictException();
        }
        database.put(user.getAccount(), user);
    }

    private static void userIdValidate(User user) {
        if (user.getId() == DEFAULT_USER_ID) {
            user.setId(++id);
        }
    }

    public static Optional<User> findByAccount(String account) {
        return Optional.ofNullable(database.get(account));
    }
}
