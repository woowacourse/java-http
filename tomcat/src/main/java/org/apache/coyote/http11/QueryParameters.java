package org.apache.coyote.http11;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

final class QueryParameters {

    private final Map<String, String> values;

    private QueryParameters(final Map<String, String> values) {
        this.values = Collections.unmodifiableMap(values);
    }

    static QueryParameters from(final String rawQuery) {
        if (rawQuery == null || rawQuery.isEmpty()) {
            return new QueryParameters(Collections.emptyMap());
        }

        final Map<String, String> values = new HashMap<>();
        for (String parameter : rawQuery.split("&")) {
            final int separatorIndex = parameter.indexOf('=');
            if (separatorIndex == -1) {
                throw new IllegalArgumentException("잘못된 Query String 형식입니다: " + parameter);
            }

            final String name = decode(parameter.substring(0, separatorIndex));
            final String value = decode(parameter.substring(separatorIndex + 1));
            values.put(name, value);
        }
        return new QueryParameters(values);
    }

    Optional<String> get(final String name) {
        return Optional.ofNullable(values.get(name));
    }

    private static String decode(final String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }
}
