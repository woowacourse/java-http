package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class HttpCookie {

    public static final String SESSION_ID = "JSESSIONID";

    private final Map<String, String> cookies = new HashMap<>();

    public HttpCookie makeSessionCookie() {
        UUID uuid = UUID.randomUUID();
        cookies.put(SESSION_ID, uuid.toString());
        return this;
    }

    public String getCookieToMessage() {
        return cookies.entrySet().stream()
                .map(entry -> String.join("=", entry.getKey(), entry.getValue()))
                .collect(Collectors.joining("; "));
    }
}
