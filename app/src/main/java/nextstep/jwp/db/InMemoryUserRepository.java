package nextstep.jwp.db;


import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import nextstep.jwp.exception.DuplicateAccountException;
import nextstep.jwp.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InMemoryUserRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(InMemoryUserRepository.class);

    private final Map<String, User> database;
    private final AtomicLong autoIncrementId;

    public InMemoryUserRepository(Map<String, User> database, AtomicLong autoIncrementId) {
        this.database = database;
        this.autoIncrementId = autoIncrementId;
    }

    public static InMemoryUserRepository initialize() {
        AtomicLong id = new AtomicLong(1);
        User user = new User(id.getAndIncrement(), "gugu", "password", "hkkang@woowahan.com");

        Map<String, User> database = new ConcurrentHashMap<>();
        database.put(user.getAccount(), user);

        return new InMemoryUserRepository(database, id);
    }

    public void save(User user) {
        if (database.containsKey(user.getAccount())) {
            LOGGER.debug("Duplicate account already exist => {}", user.getAccount());
            throw new DuplicateAccountException();
        }

        User newUser = User.withId(autoIncrementId.getAndIncrement(), user);
        database.put(newUser.getAccount(), newUser);
    }

    public Optional<User> findByAccount(String account) {
        return Optional.ofNullable(database.get(account));
    }
}
