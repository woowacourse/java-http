package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class HttpCookie {

    private static final String SEMICOLON = "; ";
    private static final String EQUAL = "=";
    private final Map<String, String> cookies;

    private HttpCookie(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public static HttpCookie from(String cookieHeaderValue) {
        Map<String, String> cookies = new HashMap<>();
        if (cookieHeaderValue == null) {
            return new HttpCookie(cookies);
        }
        String[] splitBySemicolon = cookieHeaderValue.split(SEMICOLON);
        for (String pair : splitBySemicolon) {
            String[] splitByEqual = pair.split(EQUAL, 2);
            String key = splitByEqual[0];
            String value = splitByEqual[1];
            cookies.put(key, value);
        }
        return new HttpCookie(cookies);
    }

    public static HttpCookie empty() {
        return new HttpCookie(new HashMap<>());
    }

    public void addCookie(String name, String value) {
        cookies.put(name, value);
    }

    public Optional<String> getCookie(String name) {
        if (cookies.containsKey(name)) {
            return Optional.of(cookies.get(name));
        }
        return Optional.empty();
    }
}
