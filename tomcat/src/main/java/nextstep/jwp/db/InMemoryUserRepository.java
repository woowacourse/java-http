package nextstep.jwp.db;

import java.util.concurrent.atomic.AtomicInteger;
import nextstep.jwp.model.User;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryUserRepository {

    private final AtomicInteger id = new AtomicInteger(1);
    private final Map<String, User> database = new ConcurrentHashMap<>();

    public InMemoryUserRepository() {
        final User user = new User(1L, "gugu", "password", "hkkang@woowahan.com");
        database.put(user.getAccount(), user);
    }

    public User save(User user) {
        final var id = (long) this.id.incrementAndGet();
        final var newUser = new User(id, user.getAccount(), user.getPassword(), user.getEmail());
        database.put(newUser.getAccount(), newUser);
        return newUser;
    }

    public Optional<User> findByAccount(String account) {
        return Optional.ofNullable(database.get(account));
    }
}
