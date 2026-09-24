package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class HttpHeaders {

    private final Map<String, String> headers;

    public HttpHeaders(final Map<String, String> headers) {
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

    public String valueOf(final String key) {
        return headers.get(key);
    }
}
