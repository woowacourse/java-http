package org.apache.coyote.http11;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpHeaders {

    private final Map<String, String> headers;

    private HttpHeaders(final Map<String, String> headers) {
        this.headers = headers;
    }

    public static HttpHeaders from(final List<String> headerLines) {
        final Map<String, String> headers = new LinkedHashMap<>();
        headerLines.forEach(line -> {
            final String[] lineTokens = line.split(":");
            headers.put(lineTokens[0].trim(), lineTokens[1].trim());
        });

        return new HttpHeaders(headers);
    }

    public static HttpHeaders empty() {
        return new HttpHeaders(new LinkedHashMap<>());
    }

    public void put(final String key, final String value) {
        headers.put(key, value);
    }

    public String valueOf(final String key) {
        return headers.get(key);
    }

    @Override
    public String toString() {
        return headers.entrySet()
            .stream()
            .map(header -> header.getKey() + ": " + header.getValue() + " ")
            .collect(Collectors.joining("\r\n"));
    }
}
