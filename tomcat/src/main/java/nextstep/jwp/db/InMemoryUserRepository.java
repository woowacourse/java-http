package nextstep.jwp.db;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import nextstep.jwp.model.User;

public class InMemoryUserRepository {

    private static final Map<String, User> database = new ConcurrentHashMap<>();

    static {
        final User user = new User("gugu", "password", "hkkang@woowahan.com");
        database.put(user.getAccount(), user);
    }

    public static void save(User user) {
        validateDuplicateAccount(user.getAccount());
        database.put(user.getAccount(), user);
    }

    public static Optional<User> findByAccount(String account) {
        return Optional.ofNullable(database.get(account));
    }

    private static void validateDuplicateAccount(String account) {
        if (database.containsKey(account)) {
            throw new IllegalStateException("이미 저장된 계정입니다.");
        }
    }

    private InMemoryUserRepository() {
    }
}
