package nextstep.jwp.application.db;


import nextstep.jwp.application.model.User;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class UserRepository {

    private static final Map<String, User> database = new ConcurrentHashMap<>();

    static {
        final User user = new User(1L, "gugu", "password", "hkkang@woowahan.com");
        database.put(user.getAccount(), user);
    }

    public synchronized void save(User user) {
        user.setId(checkNextId());
        database.put(user.getAccount(), user);
    }

    private synchronized Long checkNextId() {
        return (long) (database.size() + 1);
    }

    public Optional<User> findByAccount(String account) {
        return Optional.ofNullable(database.get(account));
    }
}
