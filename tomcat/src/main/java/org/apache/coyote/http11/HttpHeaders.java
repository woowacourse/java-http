package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class HttpHeaders {

    private final Map<String, String> headers;

    public HttpHeaders() {
        this.headers = new HashMap<>();
    }

    public HttpHeaders(final Map<String, String> headers) {
        this.headers = headers;
    }

    public static HttpHeaders parse(final BufferedReader bufferedReader) throws IOException {
        final var httpRequestHeaders = new HashMap<String, String>();
        var line = "";
        while ((line = bufferedReader.readLine()) != null && !line.isBlank()) {
            final int index = line.indexOf(":");
            httpRequestHeaders.put(line.substring(0, index).strip(), line.substring(index + 1).strip());
        }
        return new HttpHeaders(httpRequestHeaders);
    }

    public String get(final String key) {
        return headers.get(key);
    }

    public void set(final String key, final String value) {
        headers.put(key, value);
    }

    public Map<String, String> getHeaders() {
        return Collections.unmodifiableMap(headers);
    }
}
