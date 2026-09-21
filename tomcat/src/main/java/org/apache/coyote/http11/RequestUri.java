package org.apache.coyote.http11;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public record RequestUri(String path, Map<String, String> queryParameters) {
    public static RequestUri from(final String uri) {
        final var queryStartIndex = uri.indexOf('?');

        if (queryStartIndex < 0) {
            return new RequestUri(uri, Map.of());
        }

        final var path = uri.substring(0, queryStartIndex);
        final var queryString = uri.substring(queryStartIndex + 1);

        return new RequestUri(path, parseParameters(queryString));
    }

    public static Map<String, String> parseParameters(final String parameterString) {
        if (parameterString.isBlank()) {
            return Map.of();
        }

        final var parameters = new HashMap<String, String>();

        for (final var parameter : parameterString.split("&")) {
            final var separatorIndex = parameter.indexOf('=');

            if (separatorIndex <= 0) {
                throw new IllegalArgumentException("Invalid query parameter");
            }

            final var name = decode(parameter.substring(0, separatorIndex));
            final var value = decode(parameter.substring(separatorIndex + 1));

            parameters.put(name, value);
        }

        return Map.copyOf(parameters);
    }

    private static String decode(final String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }

    public String queryParameter(final String name) {
        return queryParameters.get(name);
    }
}
