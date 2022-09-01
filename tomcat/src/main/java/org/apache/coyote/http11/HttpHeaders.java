package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class HttpHeaders {

    public static final String KEY_VALUE_SEPARATOR = ": ";
    private final Map<String, String> headers;

    public HttpHeaders(final Map<String, String> headers) {
        this.headers = headers;
    }

    public static HttpHeaders from(final List<String> rawHeaders) {
        final Map<String, String> headers = new HashMap<>();
        rawHeaders.forEach(rawHeader -> {
            final String[] headerKeyValueSplit = rawHeader.split(KEY_VALUE_SEPARATOR);
            final String headerKey = headerKeyValueSplit[0];
            final String headerValue = headerKeyValueSplit[1];
            headers.put(headerKey, headerValue);
        });
        return new HttpHeaders(headers);
    }

    public Optional<String> getValue(final String key) {
        return Optional.ofNullable(headers.get(key));
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
