package nextstep.jwp.db;


import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import nextstep.jwp.model.User;

public class InMemoryUserRepository {

    private final Map<String, User> database;
    private long autoIncrementId;

    public InMemoryUserRepository(Map<String, User> database, long autoIncrementId) {
        this.database = database;
        this.autoIncrementId = autoIncrementId;
    }

    public static InMemoryUserRepository initialize() {
        long id = 1L;
        User user = new User(id++, "gugu", "password", "hkkang@woowahan.com");

        Map<String, User> database = new ConcurrentHashMap<>();
        database.put(user.getAccount(), user);

        return new InMemoryUserRepository(database, id);
    }

    public void save(User user) {
        User newUser = User.withId(autoIncrementId++, user);
        database.put(newUser.getAccount(), newUser);
    }

    public Optional<User> findByAccount(String account) {
        return Optional.ofNullable(database.get(account));
    }
}
