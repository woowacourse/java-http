package nextstep.jwp.db;


import nextstep.jwp.exception.DuplicatedUserException;
import nextstep.jwp.model.User;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryUserRepository {

    private static final Map<String, User> database = new ConcurrentHashMap<>();
    private static Long incrementNumber = 1L;

    static {
        final User user = new User(1L, "gugu", "password", "hkkang@woowahan.com");
        database.put(user.getAccount(), user);
    }

    public static void save(User user) {
        User newUser = new User(incrementNumber(), user.getAccount(), user.getPassword(), user.getEmail());
        database.put(newUser.getAccount(), newUser);
    }

    private static Long incrementNumber() {
        return ++incrementNumber;
    }

    public static boolean existsUser(User user) {
        return findByAccount(user.getAccount()).isPresent();
    }

    public static Optional<User> findByAccount(String account) {
        return Optional.ofNullable(database.get(account));
    }
}
