package session;

import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;

public class SessionManager {

    private static final Map<User, Session> sessions = new HashMap<>();

    public static void addSession(final User user, final Session session) {
        if (InMemoryUserRepository.findByAccount(user.getAccount()).isEmpty()) {
            throw new RuntimeException("[ERROR] 존재하지 않는 User 입니다.");
        }
        sessions.put(user, session);
    }

    public static boolean containJSessionId(final String cookieHeaderValue) {
        return Cookie.parseCookies(cookieHeaderValue).containsKey("JSESSIONID");
    }

//    public static Optional<Session> findSession(final User user) {
//        if (!sessions.containsKey(user)) {
//            return Optional.empty();
//        }
//        return Optional.of(sessions.get(user));
//    }

//    public static void removeSession(final User user) {
//        sessions.remove(user);
//    }
}
