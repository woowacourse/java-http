package nextstep.jwp.db;

import nextstep.jwp.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryUserRepository {

    private static final Map<String, User> database = new ConcurrentHashMap<>();
    private static final Logger LOG = LoggerFactory.getLogger(InMemoryUserRepository.class);

    static {
        final User user = new User(1, "gugu", "password", "hkkang@woowahan.com");
        database.put(user.getAccount(), user);
    }

    public static void save(User user) {
        database.put(user.getAccount(), user);
        LOG.info("유저 저장됨: {}", user);
    }

    public static Optional<User> findByAccount(String account) {
        final User user = database.get(account);
        LOG.info("유저 조회 성공: {}", user);
        return Optional.ofNullable(user);
    }
}
