package nextstep.jwp.db;


import nextstep.jwp.domain.User;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryUserRepository {

    private static final long INITIAL_VALUE = 1L;
    private static final Map<String, User> database = new ConcurrentHashMap<>();
    private static final AtomicLong id = new AtomicLong(INITIAL_VALUE);

    private InMemoryUserRepository() {
        throw new IllegalStateException("Utility Class");
    }

    static {
        final User user = new User( "gugu", "1234", "hkkang@woowahan.com");
        user.assignId(INITIAL_VALUE);
        database.put(user.getAccount(), user);
    }

    public static void save(User user) {
        validateExistsUser(user);
        user.assignId(id.incrementAndGet());
        database.put(user.getAccount(), user);
    }

    private static void validateExistsUser(User user) {
        if (database.containsKey(user.getAccount())) {
            throw new IllegalArgumentException("중복된 회원이 존재합니다.");
        }
    }

    public static Optional<User> findByAccount(String account) {
        return Optional.ofNullable(database.get(account));
    }

    public static int size() {
        return database.size();
    }

    public static void clear() {
        database.clear();
    }
}
