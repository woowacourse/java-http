package nextstep.jwp.db;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import nextstep.jwp.model.User;

public class InMemoryUserRepository {

    private static final Map<String, User> database = new ConcurrentHashMap<>();
    private static long AUTO_INCREMENTED_ID = 1;

    static {
        final User user = new User(AUTO_INCREMENTED_ID, "gugu", "password", "hkkang@woowahan.com");
        database.put(user.getAccount(), user);
    }

    private InMemoryUserRepository() {
    }

    public static void save(User user) {
        final User userForSave = new User(++AUTO_INCREMENTED_ID, user.getAccount(), user.getPassword(), user.getEmail());
        database.put(userForSave.getAccount(), userForSave);
    }

    public static Optional<User> findByAccount(String account) {
        return Optional.ofNullable(database.get(account));
    }
}
