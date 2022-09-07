package org.apache.coyote.http11.session;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class SessionStorage {

    private static final Map<String, Session> store = new ConcurrentHashMap<>();

    private SessionStorage() {
    }

    public static Session createSession(HttpRequest request, HttpResponse response) {
        if (hasSession(request)) {
            return store.get(request.getCookie().getValue());
        }
        return createAndSaveSession(response);
    }

    private static Session createAndSaveSession(HttpResponse response) {
        String uuid = UUID.randomUUID().toString();
        response.addCookie(new Cookie("JSESSIONID", uuid));
        Session session = new Session();
        store.put(uuid, session);
        return session;
    }

    public static boolean hasSession(HttpRequest request) {
        if (request.hasCookie()) {
            String uuid = request.getCookie().getValue();
            return store.containsKey(uuid);
        }
        return false;
    }

}
