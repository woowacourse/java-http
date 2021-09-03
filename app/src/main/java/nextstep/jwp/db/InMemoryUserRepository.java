package nextstep.jwp.db;


import nextstep.jwp.model.User;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryUserRepository {

    private static Long seq = 0L;
    private static final Map<String, User> DATABASE = new ConcurrentHashMap<>();

    static {
        final User user = new User(seq++, "admin", "1234", "hkkang@woowahan.com");
        DATABASE.put(user.getAccount(), user);
    }

    private InMemoryUserRepository() {
    }

    public static User save(User user) {
        User newToSave = user.createNewToSave(seq++);
        DATABASE.put(user.getAccount(), newToSave);
        return newToSave;
    }

    public static Optional<User> findByAccount(String account) {
        return Optional.ofNullable(DATABASE.get(account));
    }
}
