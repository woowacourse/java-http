package nextstep.jwp.http.common.session;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpCookie {

    private static final String SESSION_ID = "JSESSIONID";

    private Map<String, String> cookie;

    public HttpCookie(String rawCookie) {
        this.cookie = new HashMap<>();
        if (rawCookie == null) {
            return;
        }

        String[] cookies = rawCookie.split(";");
        for (String splitCookie : cookies) {
            String[] splitCookieKeyAndValue = splitCookie.split("=", 2);
            cookie.put(splitCookieKeyAndValue[0].trim(), splitCookieKeyAndValue[1].trim());
        }
    }

    public HttpCookie(Map<String, String> cookie) {
        this.cookie = new HashMap<>(cookie);
    }

    public boolean containsSession() {
        return cookie.containsKey(SESSION_ID);
    }

    public String getSessionId() {
        return cookie.getOrDefault(SESSION_ID, "");
    }

    public void addCookie(String sessionId) {
        cookie.put(SESSION_ID, sessionId);
    }

    public String asString() {
        return cookie.entrySet()
                .stream()
                .map(entry -> String.format("%s%s%s", entry.getKey(), "=", entry.getValue()))
                .collect(Collectors.joining(String.format("%s ", ";")));
    }
}
