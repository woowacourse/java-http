package org.apache.coyote.http11.response;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;

public class HttpResponseHeaders {
    private final Map<String, String> headers;

    public HttpResponseHeaders(Map<String, String> headers) {
        this.headers = Map.copyOf(headers);
    }

    public Optional<String> getHeader(String key) {
        return Optional.ofNullable(headers.get(key));
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        HttpResponseHeaders that = (HttpResponseHeaders) object;
        return Objects.equals(headers, that.headers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(headers);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", HttpResponseHeaders.class.getSimpleName() + "[", "]")
                .add("headers=" + headers)
                .toString();
    }
}
