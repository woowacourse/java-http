package nextstep.jwp.infrastructure.http;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpHeaders {

    private static final String KEY_DELIMITER = ": ";
    private static final String VALUE_DELIMITER = ", ";
    private final Map<String, List<String>> elements;

    public HttpHeaders(final Map<String, List<String>> elements) {
        this.elements = elements;
    }

    public static HttpHeaders of(final List<String> headers) {
        final Map<String, List<String>> values = new HashMap<>();

        for (String header : headers) {
            final List<String> splitHeader = Arrays.asList(header.split(KEY_DELIMITER));
            validateHeaderFormat(splitHeader);
            values.put(splitHeader.get(0), splitHeaderValues(splitHeader.get(1)));
        }

        return new HttpHeaders(values);
    }

    private static List<String> splitHeaderValues(final String splitValues) {
        return Arrays.asList(splitValues.split(VALUE_DELIMITER));
    }

    private static void validateHeaderFormat(final List<String> splitHeader) {
        if (splitHeader.size() != 2) {
            throw new IllegalArgumentException(String.format("잘못된 Headers 형식 입니다.(%s)", String.join("", splitHeader)));
        }
    }

    public List<String> getValue(final String key) {
        return elements.get(key);
    }

    @Override
    public String toString() {
        return elements.keySet().stream()
            .map(key -> String.join(KEY_DELIMITER, key, String.join(VALUE_DELIMITER, elements.get(key))))
            .collect(Collectors.joining(System.lineSeparator()));
    }

    public static class Builder {

        private final Map<String, List<String>> elements;

        public Builder() {
            elements = new HashMap<>();
        }

        public Builder header(final String key, final String value) {
            return header(key, new ArrayList<>(Collections.singletonList(value)));
        }

        public Builder header(final String key, final List<String> value) {
            elements.put(key, value);
            return this;
        }

        public HttpHeaders build() {
            return new HttpHeaders(elements);
        }
    }
}
