package org.apache.coyote.http11;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpCookie {

    private static final String JSESSIONID = "JSESSIONID";
    private static final String COOKIE_REGEX = "; ";
    private static final String ITEM_REGEX = "=";

    private final LinkedHashMap<String, String> values;

    private HttpCookie(final LinkedHashMap<String, String> values) {
        this.values = values;
    }

    public static HttpCookie ofJSessionId(final String id) {
        return new HttpCookie(new LinkedHashMap<>(Map.of(JSESSIONID, id)));
    }

    public static HttpCookie of(final String input) {
        LinkedHashMap<String, String> cookies = new LinkedHashMap<>();
        for (String cookie : input.split(COOKIE_REGEX)) {
            if (!cookie.contains(ITEM_REGEX)) {
                continue;
            }
            String[] splitCookie = cookie.split(ITEM_REGEX, -1);
            cookies.put(splitCookie[0], splitCookie[1]);
        }
        return new HttpCookie(cookies);
    }

    public HttpCookie add(final String key, final String value) {
        values.put(key, value);
        return this;
    }

    public String getJSessionId() {
        return values.get(JSESSIONID);
    }

    public String joinToString() {
        return values.entrySet().stream()
                .map(entry -> entry.getKey() + ITEM_REGEX + entry.getValue())
                .collect(Collectors.joining(COOKIE_REGEX));
    }
}
