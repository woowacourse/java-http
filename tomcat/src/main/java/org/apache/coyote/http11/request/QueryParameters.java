package org.apache.coyote.http11.request;

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
        for (final String parameter : rawQuery.split("&")) {
            final int separatorIndex = parameter.indexOf('=');

            String name = parameter;
            String value = "";
            if (separatorIndex != -1) {
                name = decode(parameter.substring(0, separatorIndex));
                value = decode(parameter.substring(separatorIndex + 1));
            }
            values.put(name, value);
        }
        return new QueryParameters(values);
    }

    private static String decode(final String value) {
        try {
            return URLDecoder.decode(value, StandardCharsets.UTF_8);
        } catch (final IllegalArgumentException e) {
            throw new InvalidHttpRequestException(e.getMessage(), e);
        }
    }

    Optional<String> get(final String name) {
        return Optional.ofNullable(values.get(name));
    }

    boolean isEmpty() {
        return values.isEmpty();
    }
}
