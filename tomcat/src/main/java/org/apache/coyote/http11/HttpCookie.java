package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class HttpCookie {

    private static final String JSESSIONID = "JSESSIONID";

    private final Map<String, String> cookies;

    public HttpCookie(final String cookieHeader) {
        this.cookies = parse(cookieHeader);
    }

    private Map<String, String> parse(final String cookieHeader) {
        final Map<String, String> cookies = new HashMap<>();
        if (cookieHeader == null || cookieHeader.isBlank()) {
            return cookies;
        }
        for (String pair : cookieHeader.split(";")) {
            final String[] nameValue = pair.trim().split("=", 2);
            if (nameValue.length == 2) {
                cookies.put(nameValue[0], nameValue[1]);
            }
        }
        return cookies;
    }

    public Optional<String> getJSessionId() {
        return Optional.ofNullable(cookies.get(JSESSIONID));
    }

    public static String ofJSessionId(final String sessionId) {
        return JSESSIONID + "=" + sessionId;
    }
}
