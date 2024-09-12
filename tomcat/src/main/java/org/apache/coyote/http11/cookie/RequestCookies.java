package org.apache.coyote.http11.cookie;

import static java.util.stream.Collectors.toMap;
import static org.apache.coyote.http11.cookie.HttpCookie.SESSION_KEY;

import java.util.AbstractMap.SimpleEntry;
import java.util.Map;
import java.util.Map.Entry;

public class RequestCookies {

    private final Map<String, HttpCookie> cookies;

    public RequestCookies(Map<String, HttpCookie> cookies) {
        this.cookies = cookies;
    }

    public static RequestCookies of(String requestCookies) {
        Map<String, HttpCookie> httpCookies = HttpCookie.createCookies(requestCookies)
                .stream()
                .map(cookie -> new SimpleEntry<>(cookie.getName(), cookie))
                .collect(toMap(Entry::getKey, Entry::getValue));

        return new RequestCookies(httpCookies);
    }

    public String getSessionId() {
        return get(SESSION_KEY);
    }

    public String get(String key) {
        HttpCookie cookie = cookies.get(key);
        if (cookie == null) {
            return null;
        }
        return cookie.getValue();
    }

    public void addSession(String sessionId) {
        HttpCookie sessionCookie = HttpCookie.ofSession(sessionId);
        cookies.put(SESSION_KEY, sessionCookie);
    }
}
