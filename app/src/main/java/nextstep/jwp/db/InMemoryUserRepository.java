package nextstep.jwp.db;


import nextstep.jwp.exception.AlreadyRegisteredUser;
import nextstep.jwp.model.user.domain.User;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryUserRepository {

    private static final Map<String, User> database = new ConcurrentHashMap<>();

    private static Long id = 1L;

    static {
        final User user = new User(id++, "gugu", "password", "hkkang@woowahan.com");
        database.put(user.getAccount(), user);
    }

    public static void save(User user) {
        String account = user.getAccount();
        if (database.containsKey(account)) {
            throw new AlreadyRegisteredUser(account);
        }
        database.put(account, new User(id++, user.getAccount(), user.getPassword(), user.getEmail()));
    }

    public static Optional<User> findByAccount(String account) {
        return Optional.ofNullable(database.get(account));
    }

    public static boolean isExistAccount(String account) {
        return database.containsKey(account);
    }
}
