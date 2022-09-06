package nextstep.jwp.db;

import nextstep.jwp.exception.InvalidLoginException;
import nextstep.jwp.model.User;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.coyote.http11.Http11Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InMemoryUserRepository {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final Map<String, User> database = new ConcurrentHashMap<>();

    static {
        final User user = new User(1L, "gugu", "password", "hkkang@woowahan.com");
        database.put(user.getAccount(), user);
    }

    public static void save(User user) {
        database.put(user.getAccount(), user);
    }

    public static Optional<User> findByAccount(String account) {
        return Optional.ofNullable(database.get(account));
    }

    public static User findByAccountAndPassword(String account, String password) {
        User user = findByAccount(account)
                .orElseThrow(InvalidLoginException::new);

        if (user.checkPassword(password)) {
            log.info(user.toString());
            return user;
        }
        throw new InvalidLoginException();
    }

    private InMemoryUserRepository() {}
}
