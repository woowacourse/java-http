package nextstep.jwp.db;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import nextstep.jwp.model.User;

public class InMemoryUserRepository {

    private static final Map<String, User> DATABASE = new ConcurrentHashMap<>();
    private static final AtomicLong ID_GENERATOR = new AtomicLong(1);

    static {
        final User user = new User(ID_GENERATOR.getAndIncrement(), "gugu", "password", "hkkang@woowahan.com");
        DATABASE.put(user.getAccount(), user);
    }

    public static void save(User user) {
        User saveUser = new User(ID_GENERATOR.getAndIncrement(), user.getAccount(), user.getPassword(), user.getEmail());
        DATABASE.put(user.getAccount(), saveUser);
    }

    public static Optional<User> findByAccount(String account) {
        return Optional.ofNullable(DATABASE.get(account));
    }

    private InMemoryUserRepository() {}
}
