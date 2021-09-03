package nextstep.jwp.db;


import nextstep.jwp.model.User;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryUserRepository {

    private static AtomicLong seq = new AtomicLong(0);
    private static final Map<String, User> DATABASE = new ConcurrentHashMap<>();

    static {
        final User user = new User(seq.getAndIncrement(), "admin", "1234", "hkkang@woowahan.com");
        DATABASE.put(user.getAccount(), user);
    }

    private InMemoryUserRepository() {
    }

    public static User save(User user) {
        User newToSave = user.createNewToSave(seq.getAndIncrement());
        DATABASE.put(user.getAccount(), newToSave);
        return newToSave;
    }

    public static Optional<User> findByAccount(String account) {
        return Optional.ofNullable(DATABASE.get(account));
    }
}
