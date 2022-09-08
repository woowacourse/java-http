package nextstep.jwp.infra;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import nextstep.jwp.model.User;
import nextstep.jwp.model.UserRepository;

public class InMemoryUserRepository implements UserRepository {

    private static final Map<String, User> database = new ConcurrentHashMap<>();
    private static Long index = 1L;

    static {
        final User user = new User(index++, "gugu", "password", "hkkang@woowahan.com");
        database.put(user.getAccount(), user);
    }

    public static InMemoryUserRepository getInstance() {
        return new InMemoryUserRepository();
    }

    @Override
    public User save(User user) {
        User newUser = new User(index++, user.getAccount(), user.getPassword(), user.getEmail());
        database.put(user.getAccount(), newUser);
        return database.get(user.getAccount());
    }

    @Override
    public Optional<User> findByAccount(String account) {
        return Optional.ofNullable(database.get(account));
    }

    private InMemoryUserRepository() {
    }
}
