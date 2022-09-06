package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpCookie {

    private final Map<String, String> values;

    public HttpCookie(final String cookieValue) {
        this.values = Arrays.stream(cookieValue.split("; "))
                .map(cookie -> cookie.split("="))
                .collect(Collectors.toMap(cookie -> cookie[0], cookie -> cookie[1]));
    }

    public Map<String, String> getValues() {
        return Collections.unmodifiableMap(values);
    }
}
