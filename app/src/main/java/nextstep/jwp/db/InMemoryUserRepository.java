package nextstep.jwp.db;

import nextstep.jwp.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryUserRepository {

    private static final Map<String, User> DATABASE = new ConcurrentHashMap<>();
    private static final Logger LOG = LoggerFactory.getLogger(InMemoryUserRepository.class);

    private static long sequence = 1;

    static {
        final User user = new User("gugu", "password", "hkkang@woowahan.com");
        save(user);
    }

    private InMemoryUserRepository() {
    }

    public static void save(User user) {
        validateExist(user);

        User persistUser = createNewObject(user);
        DATABASE.put(persistUser.getAccount(), persistUser);
        LOG.info("유저 저장됨: {}", persistUser);
    }

    private static void validateExist(User user) {
        if (DATABASE.containsKey(user.getAccount())) {
            LOG.info("이미 존재하는 사용자입니다. 입력 값: {}", user.getAccount());
            throw new IllegalStateException(String.format("이미 존재하는 사용자입니다. 입력 값: %s", user.getAccount()));
        }
    }

    public static Optional<User> findByAccount(String account) {
        final User user = DATABASE.get(account);
        LOG.info("유저 조회 성공: {}", user);
        return Optional.ofNullable(user);
    }

    private static User createNewObject(User user) {
        try {
            final Field idField = user.getClass()
                    .getDeclaredField("id");
            idField.setAccessible(true);
            idField.setLong(user, sequence++);
            return user;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new IllegalStateException("찾을 수 없는 필드입니다.");
        }
    }
}
