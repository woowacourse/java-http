package nextstep.jwp.db;

import nextstep.jwp.model.User;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class InMemoryUserRepository {

    private static final Map<String, User> database = new ConcurrentHashMap<>();
    private static long idCounter = 1L;

    static {
        final User user = new User(idCounter++, "gugu", "password", "hkkang@woowahan.com");
        database.put(user.getAccount(), user);
    }

    private InMemoryUserRepository() {
    }

    public static void save(final User user) {
        user.setId(idCounter++);
        database.put(user.getAccount(), user);
    }

    public static Optional<User> findByAccount(final String account) {
        return Optional.ofNullable(database.get(account));
    }

    public static void cleanUp() {
        final List<String> accounts = database.keySet()
                .stream()
                .filter(key -> !key.equals("gugu"))
                .collect(Collectors.toList());

        accounts.forEach(database::remove);
    }
}
