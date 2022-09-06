package nextstep.jwp.http.common;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    public static final String JSESSIONID = "JSESSIONID";

    private static Map<String, Session> store = new ConcurrentHashMap<>();

    public static HttpCookie createCookie() {
        String id = UUID.randomUUID().toString();

        return new HttpCookie(JSESSIONID, id);
    }

    public static void addSession(final String id, final Session session) {
        store.put(id, session);
    }
}
