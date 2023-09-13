package nextstep.jwp.db;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import nextstep.jwp.model.User;

public class InMemorySession {

    private static final Map<UUID, User> session = new HashMap<>();

    private InMemorySession(){}

    public static String login(User user) {
        UUID uuid = UUID.randomUUID();
        session.put(uuid, user);
        return uuid.toString();
    }

    public static boolean isLogin(String id) {
        for (UUID uuid : session.keySet()) {
            if (uuid.toString().equals(id)) {
                return true;
            }
        }
        return false;
    }
}
