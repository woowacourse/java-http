package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HttpCookie {
    private final Map<String, String> map = new HashMap<>();

    public static HttpCookie parse(final String cookieHeaders) {
        HttpCookie cookie = new HttpCookie();
        if (cookieHeaders == null || cookieHeaders.isEmpty()) {
            return cookie;
        }
        String[] pairs = cookieHeaders.split(";");
        for (String rawPair : pairs) {
            String pair = rawPair.trim();
            int equals = pair.indexOf('=');
            if (equals > 0) {
                String name = pair.substring(0, equals);
                String value = pair.substring(equals + 1);
                cookie.map.put(name, value);
            }
        }
        return cookie;
    }

    public static String newSessionId() {
        return UUID.randomUUID().toString();
    }

    public static String buildSetCookieHeader(final String jsessionId) {
        if (jsessionId == null || jsessionId.isEmpty()) {
            return "";
        }
        return "JSESSIONID=" + jsessionId + "; Path=/; HttpOnly";
    }

    public String get(String name) {
        return map.get(name);
    }
}
