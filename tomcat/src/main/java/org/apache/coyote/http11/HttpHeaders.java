package org.apache.coyote.http11;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class HttpHeaders {

    private final Map<String, List<String>> headers;

    public HttpHeaders() {
        this.headers = Collections.emptyMap();
    }

    public HttpHeaders(final Map<String, List<String>> headers) {
        this.headers = headers;
    }

    public List<String> getValues(final String name) {
        return headers.getOrDefault(name, List.of());
    }

    public Optional<String> get(final String name) {
        List<String> values = getValues(name);
        return values.isEmpty() ? Optional.empty() : Optional.of(values.getFirst());
    }

}
