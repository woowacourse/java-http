package nextstep.jwp.infrastructure;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import nextstep.jwp.domain.model.User;
import nextstep.jwp.domain.model.UserRepository;

public class InMemoryUserRepository implements UserRepository {

    private static final Map<Long, User> database = new ConcurrentHashMap<>();
    private static Long seq = 1L;

    private static final InMemoryUserRepository instance = new InMemoryUserRepository();

    private InMemoryUserRepository() {
    }

    public static InMemoryUserRepository getInstance() {
        return instance;
    }

    static {
        final Long id = seq++;
        final User user = new User(id, "gugu", "password", "hkkang@woowahan.com");
        database.put(id, user);
    }

    @Override
    public User save(final User user) {
        final User newUser = new User(seq++, user.getAccount(), user.getPassword(), user.getEmail());
        database.put(newUser.getId(), newUser);
        return newUser;
    }

    @Override
    public Optional<User> findByAccount(final String account) {
        return database.values()
                .stream()
                .filter(user -> user.getAccount().equals(account))
                .findAny();
    }
}
