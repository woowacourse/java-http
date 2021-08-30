package nextstep.jwp.db;


import nextstep.jwp.model.User;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryUserRepository {

    private static final Map<String, User> database = new ConcurrentHashMap<>();

    static {
        final User user = new User(1, "gugu", "password", "hkkang@woowahan.com");
        database.put(user.getAccount(), user);
    }

    public static User save(User user) {
        Long nextId = 1L + database.values().stream()
                .mapToLong(User::getId)
                .max()
                .orElseThrow(() -> new RuntimeException("PK 값을 산출할 수 없습니다."));
        // todo 예외 생성

        User persistedUser = new User(nextId, user.getAccount(), user.getPassword(), user.getEmail());
        database.put(persistedUser.getAccount(), persistedUser);

        return persistedUser;
    }

    public static Optional<User> findByAccount(String account) {
        return Optional.ofNullable(database.get(account));
    }
}
