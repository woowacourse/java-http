package org.apache.coyote.web;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    private static Map<String, Object> store = new ConcurrentHashMap<>();

    public static void createSession(final Object object, final Response response) {
        String id = UUID.randomUUID().toString();

        store.put(id, object);
        response.addCookie(new Cookie("JSESSIONID", id));
    }
}
