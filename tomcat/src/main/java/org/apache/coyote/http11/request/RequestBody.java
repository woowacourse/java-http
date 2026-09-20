package org.apache.coyote.http11.request;

import org.apache.coyote.http11.InvalidRequestException;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class RequestBody {
    private static final String PARAMETER_DELIMITER = "&";
    private static final String NAME_VALUE_DELIMITER = "=";
    private static final String EMPTY_VALUE = "";
    private static final int NOT_FOUND = -1;

    private RequestBody(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    private final Map<String, String> parameters;

    public static RequestBody from(final String rawBody) {
        return new RequestBody(parseQueryString(rawBody));
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


    public boolean hasParameters() {
        return !parameters.isEmpty();
    }

    public Optional<String> getParameter(final String name) {
        return Optional.ofNullable(parameters.get(name));
    }
}
