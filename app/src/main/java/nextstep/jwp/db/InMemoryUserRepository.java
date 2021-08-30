package nextstep.jwp.db;


import nextstep.jwp.model.User;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryUserRepository {

    private static Long seq = 0L;
    private static final Map<String, User> database = new ConcurrentHashMap<>();

    static {
        final User user = new User(seq++, "gugu", "password", "hkkang@woowahan.com");
        database.put(user.getAccount(), user);
    }

    private InMemoryUserRepository() {
    }

    public static User save(User user) {
        User newToSave = user.createNewToSave(seq++);
        database.put(user.getAccount(), newToSave);
        return newToSave;
    }

    public static Optional<User> findByAccount(String account) {
        return Optional.ofNullable(database.get(account));
    }
}
