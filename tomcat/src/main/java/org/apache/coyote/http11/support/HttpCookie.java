package org.apache.coyote.http11.support;

import static org.apache.coyote.http11.web.request.HttpRequest.J_SESSION;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class HttpCookie {

    private static final String COOKIE_DELIMITER = "; ";
    private static final String KEY_VALUE_DELIMITER = "=";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, String> values;

    private HttpCookie(final Map<String, String> values) {
        this.values = values;
    }

    public static HttpCookie create() {
        final Map<String, String> values = new HashMap<>();
        values.put(J_SESSION, String.valueOf(UUID.randomUUID()));

        return new HttpCookie(values);
    }

    public static HttpCookie from(final String cookie) {
        final String[] splitCookie = cookie.split(COOKIE_DELIMITER);
        final Map<String, String> values = Arrays.stream(splitCookie)
                .map(it -> it.split(KEY_VALUE_DELIMITER))
                .collect(Collectors.toMap(
                        it -> it[KEY_INDEX],
                        it -> it[VALUE_INDEX]
                ));
        return new HttpCookie(values);
    }

    public String getJSessionId() {
        return values.get(J_SESSION);
    }

    public String format() {
        return values.keySet()
                .stream()
                .map(key -> key + "=" + values.get(key))
                .collect(Collectors.joining("; "));
    }
}
