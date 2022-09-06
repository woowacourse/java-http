package org.apache.coyote.http11.request;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpCookie {

    private final Map<String, String> values;

    private HttpCookie(Map<String, String> values) {
        this.values = values;
    }

    public static HttpCookie of(String rawCookies) {
        String[] splitCookies = rawCookies.split("; ");
        return new HttpCookie(Arrays.stream(splitCookies)
                .map(splitCookie -> splitCookie.split("="))
                .collect(Collectors.toMap(value -> value[0], value -> value[1].trim())));
    }

    public static HttpCookie emptyCookie() {
        return new HttpCookie(Collections.emptyMap());
    }

    public boolean existsJSessionId() {
        return values.containsKey("JSESSIONID");
    }

    public String get(String name) {
        return values.get(name);
    }
}
