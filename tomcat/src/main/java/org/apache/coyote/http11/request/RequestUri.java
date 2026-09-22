package org.apache.coyote.http11.request;

import org.apache.coyote.http11.InvalidRequestException;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class RequestUri {

    private static final String QUERY_DELIMITER = "?";
    private static final String PARAMETER_DELIMITER = "&";
    private static final String NAME_VALUE_DELIMITER = "=";
    private static final String EMPTY_VALUE = "";
    private static final int NOT_FOUND = -1;

    private final String path;
    private final Map<String, String> queryParameters;

    private RequestUri(final String path, final Map<String, String> queryParameters) {
        this.path = path;
        this.queryParameters = queryParameters;
    }

    public static RequestUri from(final String uri) {
        final int queryIndex = uri.indexOf(QUERY_DELIMITER);

        if (queryIndex == NOT_FOUND) {
            return new RequestUri(decodePath(uri), Map.of());
        }
        final String path = uri.substring(0, queryIndex);
        final String queryString = uri.substring(queryIndex + 1);
        return new RequestUri(decodePath(path), parseQueryString(queryString));
    }

    private static Map<String, String> parseQueryString(final String queryString) {
        final Map<String, String> parameters = new HashMap<>();
        for (final String pair : queryString.split(PARAMETER_DELIMITER)) {
            if (pair.isBlank()) {
                continue;
            }
            final int delimiterIndex = pair.indexOf(NAME_VALUE_DELIMITER);
            final String name = delimiterIndex == NOT_FOUND ? pair : pair.substring(0, delimiterIndex);
            final String value = delimiterIndex == NOT_FOUND ? EMPTY_VALUE : pair.substring(delimiterIndex + 1);
            parameters.putIfAbsent(decodeFormValue(name), decodeFormValue(value));
        }
        return Map.copyOf(parameters);
    }

    private static String decodeFormValue(final String value) {
        try {
            return URLDecoder.decode(value, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            throw new InvalidRequestException("잘못된 인코딩입니다: " + value, e);
        }
    }

    private static String decodePath(final String path) {
        if (!path.contains("%")) {
            return path;
        }
        try {
            return URLDecoder.decode(path.replace("+", "%2B"), StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            throw new InvalidRequestException("잘못된 인코딩입니다: " + path, e);
        }
    }

    public boolean hasQueryParameters() {
        return !queryParameters.isEmpty();
    }

    public String getPath() {
        return path;
    }

    public Optional<String> getQueryParameter(final String name) {
        return Optional.ofNullable(queryParameters.get(name));
    }
}