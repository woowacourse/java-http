package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.catalina.Session;

public class HttpCookie {

    public static final String SESSION_KEY = "JSESSIONID";

    private Map<String, String> keyValues;

    public static HttpCookie from(Session session) {
        final var keyValues = new HashMap<String, String>();
        keyValues.put(SESSION_KEY, session.getId());
        return new HttpCookie(keyValues);
    }

    public HttpCookie(Map<String, String> keyValues) {
        this.keyValues = keyValues;
    }

    public String getCookie(String key) {
        return keyValues.get(key);
    }

    @Override
    public String toString() {
        return keyValues.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("; "));
    }

}
