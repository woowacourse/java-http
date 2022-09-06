package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import org.apache.coyote.http11.session.HttpCookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHeaders {

    private static final Logger log = LoggerFactory.getLogger(RequestHeaders.class);
    private static final int DEFAULT_HEADER_FIELD_LENGTH = 2;
    private static final String COOKIE = "Cookie";

    private final Map<String, Object> headers;

    private RequestHeaders(final Map<String, Object> headers) {
        this.headers = headers;
    }

    public static RequestHeaders from(final BufferedReader bufferedReader) {
        try {
            return new RequestHeaders(parseHeaders(bufferedReader));
        } catch (final IOException e) {
            log.error("invalid request header", e);
            throw new IllegalArgumentException("올바른 HTTP Request Header 형식이 아닙니다.");
        }
    }

    private static Map<String, Object> parseHeaders(final BufferedReader bufferedReader) throws IOException {
        final Map<String, Object> headers = new HashMap<>();

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
        return Optional.ofNullable(headers.get(fieldName))
                .orElseThrow(() -> new NoSuchElementException("Header Field에 key 값이 존재하지 않습니다."));
    }

    public Optional<String> findCookie(final String name) {
        final Optional<Object> cookies = Optional.ofNullable(headers.get(COOKIE));
        if (cookies.isEmpty()) {
            return Optional.empty();
        }

        return ((HttpCookie) cookies.get()).findCookie(name);
    }

    public Map<String, Object> getHeaders() {
        return headers;
    }
}
