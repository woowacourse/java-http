package session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;

public class SessionManager {

    private static final Map<User, Session> sessions = new ConcurrentHashMap<>();

    public static void addSession(final User user, final Session session) {
        if (InMemoryUserRepository.findByAccount(user.getAccount()).isEmpty()) {
            throw new RuntimeException("[ERROR] 존재하지 않는 User 입니다.");
        }
        sessions.put(user, session);
    }
}
