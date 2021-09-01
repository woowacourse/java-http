package nextstep.jwp.infrastructure.http;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import nextstep.jwp.infrastructure.http.request.Cookie;

public class Headers {

    private static final String KEY_DELIMITER = ": ";
    private static final String VALUE_DELIMITER = ", ";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;
    private static final int SPLIT_HEADER_SIZE = 2;
    private static final String COOKIE = "Cookie";

    private final Map<String, String> elements;

    public Headers() {
        this(new LinkedHashMap<>());
    }

    public Headers(final Map<String, String> elements) {
        this.elements = elements;
    }

    public static Headers of(final List<String> headers) {
        final Map<String, String> elements = new LinkedHashMap<>();

        for (String header : headers) {
            final List<String> splitHeader = Arrays.asList(header.split(KEY_DELIMITER));
            validateHeaderFormat(splitHeader);
            elements.put(splitHeader.get(KEY_INDEX), splitHeader.get(VALUE_INDEX));
        }

        return new Headers(elements);
    }

    private static void validateHeaderFormat(final List<String> splitHeader) {
        if (splitHeader.size() != SPLIT_HEADER_SIZE) {
            throw new IllegalArgumentException(String.format("잘못된 Headers 형식 입니다.(%s)", String.join("", splitHeader)));
        }
    }

    public void add(final String key, final String value) {
        elements.put(key, value);
    }

    public boolean hasKey(final String key) {
        return elements.containsKey(key);
    }

    public Cookie getCookie() {
        if (elements.containsKey(COOKIE)) {
            return Cookie.of(elements.get(COOKIE));
        }
        return new Cookie();
    }

    public String getValue(final String key) {
        return elements.get(key);
    }

    @Override
    public String toString() {
        return elements.keySet().stream()
            .map(key -> String.format("%s%s%s ", key, KEY_DELIMITER, String.join(VALUE_DELIMITER, elements.get(key))))
            .collect(Collectors.joining("\r\n"));
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Headers headers = (Headers) o;
        return Objects.equals(elements, headers.elements);
    }

    @Override
    public int hashCode() {
        return Objects.hash(elements);
    }

    public static class Builder {

        private final Map<String, String> elements;

        public Builder() {
            elements = new LinkedHashMap<>();
        }

        public Builder header(final String key, final String value) {
            elements.put(key, value);
            return this;
        }

        public Headers build() {
            return new Headers(elements);
        }
    }
}
