package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class HttpHeaders {

    public static final String LOCATION = "Location";
    public static final String COOKIE = "Cookie";
    public static final String SET_COOKIE = "Set-Cookie";
    public static final String CONTENT_LENGTH = "Content-Length";
    public static final String CONTENT_TYPE = "Content-Type";

    private static final String KEY_VALUE_SEPARATOR = ": ";
    private static final String CRLF = "\r\n";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, String> headers;

    public HttpHeaders(final Map<String, String> headers) {
        this.headers = headers;
    }

    public static HttpHeaders from(final List<String> rawHeaders) {
        final Map<String, String> headers = new HashMap<>();
        rawHeaders.forEach(rawHeader -> {
            final String[] headerKeyValueSplit = rawHeader.split(KEY_VALUE_SEPARATOR);
            final String headerKey = headerKeyValueSplit[KEY_INDEX];
            final String headerValue = headerKeyValueSplit[VALUE_INDEX].trim();
            headers.put(headerKey, headerValue);
        });
        return new HttpHeaders(headers);
    }

    public static HttpHeaders empty() {
        return new HttpHeaders(new HashMap<>());
    }

    public Optional<String> getValue(final String key) {
        return Optional.ofNullable(headers.get(key));
    }

    public String toHttpMessageHeader() {
        return headers.keySet()
                .stream()
                .map(key -> key + KEY_VALUE_SEPARATOR + headers.get(key) + " ")
                .collect(Collectors.joining(CRLF));
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof HttpHeaders)) {
            return false;
        }
        final HttpHeaders that = (HttpHeaders) o;
        return Objects.equals(headers, that.headers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(headers);
    }
}
