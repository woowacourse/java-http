package nextstep.jwp.db;


import nextstep.jwp.model.User;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryUserRepository {

    private static long INDEX = 1;
    private static final Map<String, User> database = new ConcurrentHashMap<>();

    static {
        final User user = new User(1, "gugu", "password", "hkkang@woowahan.com");
        database.put(user.getAccount(), user);
    }

    public static void save(User user) {
        injectAutoIncrementIndex(user);
        database.put(user.getAccount(), user);
    }

    private static void injectAutoIncrementIndex(User user) {
        try {
            Class<? extends User> userClass = user.getClass();
            Field id = userClass.getDeclaredField("id");
            id.setAccessible(true);
            id.setLong(user, ++INDEX);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("USER INSERT ERROR");
        }
    }

    public static boolean login(String account, String password) {
        if (!database.containsKey(account)) {
            return false;
        }
        User user = database.get(account);
        return user.checkPassword(password);
    }

    public static Optional<User> findByAccount(String account) {
        return Optional.ofNullable(database.get(account));
    }
}
