package org.apache.coyote.http11.common;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class HttpHeaders {
    public static final String DELIMITER = ": ";

    private Map<String, String> headers;

    public HttpHeaders() {
        headers = new LinkedHashMap<>();
    }

    private HttpHeaders(final Map<String, String> headers) {
        this.headers = headers;
    }

    public static HttpHeaders create(final List<String> lines) {
        Map<String, String> headers = new LinkedHashMap<>();
        for (String line : lines) {
            String[] split = line.split(DELIMITER);
            headers.put(split[0], split[1]);
        }

        return new HttpHeaders(headers);
    }

    public static HttpHeaders create(final Map<String, String> headers) {
        return new HttpHeaders(new LinkedHashMap<>(headers));
    }

    public void addHeader(HttpHeaderName header, String value) {
        headers.put(header.getName(), value);
    }

    public Optional<String> getHeader(String name) {
        return Optional.ofNullable(headers.get(name));
    }

    @Override
    public String toString() {
        return headers.entrySet().stream()
                .map(entry -> entry.getKey() + DELIMITER + entry.getValue() + " ")
                .collect(Collectors.joining(System.lineSeparator()));
    }
}
