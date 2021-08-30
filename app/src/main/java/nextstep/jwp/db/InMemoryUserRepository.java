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

    private InMemoryUserRepository() {
    }

    public static void save(User user) {
        User saveUser = new User((long) database.size() + 1, user.getAccount(), user.getPassword(), user.getEmail());
        database.put(saveUser.getAccount(), saveUser);
    }

    public static Optional<User> findByAccount(String account) {
        return Optional.ofNullable(database.get(account));
    }

    public static boolean existUserByAccountAndPassword(String account, String password) {
        return database.get(account).checkPassword(password);
    }
}
