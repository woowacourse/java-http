package org.apache.coyote.http11.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.coyote.http11.session.HttpCookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Headers {

    private static final Logger log = LoggerFactory.getLogger(Headers.class);
    private static final String COOKIE = "Cookie";
    private static final int DEFAULT_HEADER_FIELD_LENGTH = 2;

    private final Map<String, Object> values;

    private Headers(final Map<String, Object> values) {
        this.values = values;
    }

    public static Headers from(final Entry<String, Object>... headers) {
        final Map<String, Object> values = new LinkedHashMap<>();
        for (final Entry<String, Object> header : headers) {
            values.put(header.getKey(), header.getValue());
        }
        return new Headers(values);
    }

    public static Headers from(final BufferedReader bufferedReader) {
        try {
            return new Headers(parseHeaders(bufferedReader));
        } catch (final IOException e) {
            log.error("invalid request header", e);
            throw new IllegalArgumentException("올바른 HTTP Request Header 형식이 아닙니다.");
        }
    }

    private static Map<String, Object> parseHeaders(final BufferedReader bufferedReader) throws IOException {
        final Map<String, Object> headers = new LinkedHashMap<>();

        while (bufferedReader.ready()) {
            final String line = bufferedReader.readLine();
            Objects.requireNonNull(line);
            if ("".equals(line)) {
                break;
            }
            addHeader(headers, line);
        }
        return headers;
    }

    private static void addHeader(final Map<String, Object> headers, final String line) {
        final String[] field = line.split(": ");
        validateFieldLength(field);
        if (field[0].equals(COOKIE)) {
            final HttpCookie httpCookie = HttpCookie.from(field[1]);
            headers.put(COOKIE, httpCookie);
            return;
        }
        headers.put(field[0], field[1].trim());
    }

    private static void validateFieldLength(final String[] field) {
        if (field.length != DEFAULT_HEADER_FIELD_LENGTH) {
            throw new IllegalArgumentException("올바른 Header Field 형식이 아닙니다.");
        }
    }

    public Object findField(final String fieldName) {
        return Optional.ofNullable(values.get(fieldName))
                .orElseThrow(() -> new NoSuchElementException("Header Field에 key 값이 존재하지 않습니다."));
    }

    public Optional<String> findCookie(final String name) {
        final Optional<Object> cookies = Optional.ofNullable(values.get(COOKIE));
        if (cookies.isEmpty()) {
            return Optional.empty();
        }

        return ((HttpCookie) cookies.get()).findCookie(name);
    }

    public Map<String, Object> getValues() {
        return values;
    }

    @Override
    public String toString() {
        return values.entrySet()
                .stream()
                .map(e -> e.getKey() + ": " + e.getValue())
                .collect(Collectors.joining("\r\n"));
    }
}
