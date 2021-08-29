package nextstep.jwp.db;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import nextstep.jwp.exception.user.OutOfUserIdException;
import nextstep.jwp.exception.user.UserReflectionException;
import nextstep.jwp.model.User;

public class InMemoryUserRepository {

    private static final Map<String, User> DATABASE = new ConcurrentHashMap<>();

    private static Long id = 1L;

    static {
        final User user = new User(1L, "gugu", "password", "hkkang@woowahan.com");
        DATABASE.put(user.getAccount(), user);
    }

    public static void save(User user) {
        User recreatedUser = recreateWithId(user);
        DATABASE.put(recreatedUser.getAccount(), recreatedUser);
    }

    private static User recreateWithId(User user) {
        if (isMaximum()) {
            throw new OutOfUserIdException();
        }
        try {
            Class userClass = user.getClass();
            Field userId = userClass.getDeclaredField("id");
            if (userId.trySetAccessible()) {
                userId.set(user, ++id);
            }
        } catch (Exception e) {
            throw new UserReflectionException();
        }

        return user;
    }

    private static boolean isMaximum() {
        return id == (int) Math.pow(2, 63) - 1;
    }

    public static Optional<User> findByAccount(String account) {
        return Optional.ofNullable(DATABASE.get(account));
    }
}
