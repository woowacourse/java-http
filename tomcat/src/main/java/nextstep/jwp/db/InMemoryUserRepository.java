package nextstep.jwp.db;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import nextstep.jwp.model.User;

public class InMemoryUserRepository {

    private AtomicLong id = new AtomicLong(1L);
    private final Map<String, User> database = new ConcurrentHashMap<>();

    public InMemoryUserRepository() {
        final User user = new User(id.getAndIncrement(), "gugu", "password", "hkkang@woowahan.com");
        database.put(user.getAccount(), user);
    }

    public void save(User user) {
        if (!user.hasId()) {
            user = user.setId(id.getAndIncrement());
        }
        database.put(user.getAccount(), user);
    }

    public Optional<User> findByAccount(String account) {
        if (account == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(database.get(account));
    }
}
