package nextstep.jwp.db;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import nextstep.jwp.model.User;

public class InMemoryUserRepository {

    private static final Map<String, User> database = new ConcurrentHashMap<>();

    static {
        final User user = new User(1, "gugu", "password", "hkkang@woowahan.com");
        database.put(user.getAccount(), user);
    }

    public static void save(User user) {
        user.setId(findCurrentId());
        database.put(user.getAccount(), user);
    }

    public static Optional<User> findByAccount(String account) {
        return Optional.ofNullable(database.get(account));
    }

    public static long findCurrentId() {
        List<User> userList = new ArrayList<User>(database.values());
        final long maxId = userList.stream()
                .mapToLong(User::getId)
                .max()
                .orElse(1);
        return maxId + 1;
    }
}
