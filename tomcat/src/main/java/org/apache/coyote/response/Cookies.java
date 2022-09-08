package org.apache.coyote.response;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Cookies {

    private static final String COOKIE_SEPARATOR = "; ";

    private final Map<String, Cookie> cookies;

    private Cookies(final Map<String, Cookie> cookies) {
        this.cookies = cookies;
    }

    public static Cookies init() {
        return new Cookies(new HashMap<>());
    }

    public static Cookies of(final String request) {
        return new Cookies(Arrays.stream(request.split(COOKIE_SEPARATOR))
                .map(Cookie::from)
                .collect(Collectors.toMap(Cookie::getName, Function.identity())));
    }

    public void add(final Cookie cookie) {
        cookies.put(cookie.getName(), cookie);
    }

    public Cookie find(final String key) {
        return cookies.get(key);
    }
    
    public boolean isEmpty() {
        return cookies.isEmpty();
    }

    public String toHeaders() {
        StringJoiner stringJoiner = new StringJoiner(COOKIE_SEPARATOR);
        for (Cookie value : cookies.values()) {
            stringJoiner.add(value.toHeader());
        }
        return stringJoiner.toString();
    }
}
