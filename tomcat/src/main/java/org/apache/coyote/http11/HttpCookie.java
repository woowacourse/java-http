package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;

public class HttpCookie {

    private static final SessionManager sessionManager = SessionManager.getInstance();
    private static final String JSESSIONID = "JSESSIONID";


    private final Map<String, String> values;

    private HttpCookie(Map<String, String> values) {
        this.values = values;
    }

    public static HttpCookie from(String cookies) {
        Map<String, String> values = new HashMap<>();
        for (String cookie : cookies.split(";")) {
            if (cookie.isEmpty()) {
                break;
            }
            String key = cookie.split("=")[0];
            String value = cookie.split("=")[1];
            values.put(key, value);
        }
        return new HttpCookie(values);
    }

    public Session getSession(boolean isCreate) {
        if (has(JSESSIONID)) {
            String sessionId = getValue(JSESSIONID);
            return sessionManager.findSession(sessionId);
        }
        if (isCreate) {
            Session session = new Session(UUID.randomUUID().toString());
            sessionManager.add(session);
            return session;
        }
        return null;
    }

    public String getValue(String key) {
        if (has(key)) {
            return values.get(key);
        }
        throw new IllegalArgumentException("쿠키가 존재하지 않습니다.");
    }

    public boolean has(String key) {
        return values.containsKey(key);
    }

    public boolean isEmpty() {
        return values.isEmpty();
    }

    public Map<String, String> getCookies() {
        return values;
    }
}
