package nextstep.jwp.db;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import nextstep.jwp.model.User;

public class InMemoryUserRepository {

    private final Map<String, User> database = new ConcurrentHashMap<>();

    public InMemoryUserRepository() {
        final User user = new User(1L, "gugu", "password", "hkkang@woowahan.com");
        database.put(user.getAccount(), user);
    }

    public void save(User user) {
        database.put(user.getAccount(), user);
    }

    public Optional<User> findByAccount(String account) {
        return Optional.ofNullable(database.get(account));
    }
}
