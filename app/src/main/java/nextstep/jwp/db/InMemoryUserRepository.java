package nextstep.jwp.db;


import nextstep.jwp.model.User;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryUserRepository {
    private static final Map<String, User> database = new ConcurrentHashMap<>();

    private static long index = 0L;

    static {
        final User user = new User(index++,"gugu", "password", "hkkang@woowahan.com");
        database.put(user.getAccount(), user);
    }

    public static void save(User user) {
        injectAutoIncrementIndex(user);
        database.put(user.getAccount(), user);
    }

    public static Optional<User> findByAccount(String account) {
        return Optional.ofNullable(database.get(account));
    }

    private static void injectAutoIncrementIndex(User user) {
        try {
            Class<? extends User> userClass = user.getClass();
            Field id = userClass.getDeclaredField("id");
            id.setAccessible(true);
            id.setLong(user, ++index);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new IllegalArgumentException("유저 id 추가에 문제가 발생했습니다.");
        }
    }
}
