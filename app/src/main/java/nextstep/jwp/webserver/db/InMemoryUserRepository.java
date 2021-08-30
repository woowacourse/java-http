package nextstep.jwp.webserver.db;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import nextstep.jwp.webserver.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InMemoryUserRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(InMemoryUserRepository.class);

    private static final Map<String, User> database = new ConcurrentHashMap<>();

    private static long lastInsertId = 1;

    static {
        final User user = new User(1L, "gugu", "password", "hkkang@woowahan.com");
        database.put(user.getAccount(), user);
    }

    private InMemoryUserRepository() {}

    public static void save(User user) {
        user.assignId(++lastInsertId);
        database.put(user.getAccount(), user);

        LOGGER.debug("lasInsertId : {}", lastInsertId);
    }

    public static Optional<User> findByAccount(String account) {
        return Optional.ofNullable(database.get(account));
    }
}
