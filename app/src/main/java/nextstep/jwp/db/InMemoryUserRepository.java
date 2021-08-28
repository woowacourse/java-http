package nextstep.jwp.db;

import nextstep.jwp.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryUserRepository {

    private static final Map<String, User> DATABASE = new ConcurrentHashMap<>();
    private static final Logger LOG = LoggerFactory.getLogger(InMemoryUserRepository.class);

    static {
        final User user = new User(1, "gugu", "password", "hkkang@woowahan.com");
        DATABASE.put(user.getAccount(), user);
    }

    private InMemoryUserRepository() {
    }

    public static void save(User user) {
        DATABASE.put(user.getAccount(), user);
        LOG.info("유저 저장됨: {}", user);
    }

    public static Optional<User> findByAccount(String account) {
        final User user = DATABASE.get(account);
        LOG.info("유저 조회 성공: {}", user);
        return Optional.ofNullable(user);
    }
}
