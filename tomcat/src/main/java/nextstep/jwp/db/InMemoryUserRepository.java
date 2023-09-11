package nextstep.jwp.db;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import nextstep.jwp.model.User;

public class InMemoryUserRepository {

    private static final Map<String, User> database = new ConcurrentHashMap<>();
    private static final AtomicLong auto_increment = new AtomicLong(1);

    static {
        final User user = new User(auto_increment.getAndIncrement(), "gugu", "password", "hkkang@woowahan.com");
        database.put(user.getAccount(), user);
    }

    public static void save(User user) {
        final User saveUser = new User(
                auto_increment.getAndIncrement(),
                user.getAccount(),
                user.getPassword(),
                user.getEmail()
        );
        database.put(saveUser.getAccount(), saveUser);
    }

    public static Optional<User> findByAccount(String account) {
        return Optional.ofNullable(database.get(account));
    }

    private InMemoryUserRepository() {
    }
}
