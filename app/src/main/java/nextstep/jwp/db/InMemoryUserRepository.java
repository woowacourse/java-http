package nextstep.jwp.db;


import java.util.Objects;
import nextstep.jwp.model.User;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryUserRepository {

    private static Long id = 0L;
    private static final Map<String, User> database = new ConcurrentHashMap<>();

    static {
        final User user = new User(id++, "gugu", "password", "hkkang@woowahan.com");
        database.put(user.getAccount(), user);
    }

    public static void save(User user) {
        if(Objects.isNull(user.getId())){
            user = new User(
                id++,
                user.getAccount(),
                user.getPassword(),
                user.getEmail()
            );
        }
        database.put(user.getAccount(), user);
    }

    public static Optional<User> findByAccount(String account) {
        return Optional.ofNullable(database.get(account));
    }
}
