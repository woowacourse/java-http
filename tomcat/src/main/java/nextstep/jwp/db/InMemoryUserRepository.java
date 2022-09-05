package nextstep.jwp.db;

import java.util.NoSuchElementException;
import nextstep.jwp.model.User;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryUserRepository {

    private static final Map<String, User> database = new ConcurrentHashMap<>();

    static {
        final User user = new User(1L, "gugu", "password", "hkkang@woowahan.com");
        database.put(user.getAccount(), user);
    }

    public static User getByAccount(String account) {
        return findByAccount(account)
                .orElseThrow(() -> new NoSuchElementException("해당 account를 가진 user가 존재하지 않습니다. : " + account));
    }

    public static Optional<User> findByAccount(String account) {
        return Optional.ofNullable(database.get(account));
    }

    private InMemoryUserRepository() {}
}
