package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class HttpCookie {

    private static final String COOKIE_DELIMITER = ";";
    private static final String NAME_VALUE_DELIMITER = "=";

    private final Map<String, String> values;

    private HttpCookie(final Map<String, String> values) {
        this.values = Map.copyOf(values);
    }

    public static HttpCookie from(final String cookieHeader) {
        final Map<String, String> cookies =
                new HashMap<>();

        if (cookieHeader == null
                || cookieHeader.isBlank()) {
            return new HttpCookie(cookies);
        }

        for (String cookie :
                cookieHeader.split(COOKIE_DELIMITER)) {

            final String[] pair =
                    cookie.trim()
                            .split(NAME_VALUE_DELIMITER, 2);

            if (pair.length != 2) {
                continue;
            }

            final String name =
                    pair[0].trim();

            final String value =
                    pair[1].trim();

            if (name.isBlank()) {
                continue;
            }

            cookies.put(name, value);
        }

        return new HttpCookie(cookies);
    }

    public Optional<String> get(
            final String name
    ) {
        return Optional.ofNullable(
                values.get(name)
        );
    }
}