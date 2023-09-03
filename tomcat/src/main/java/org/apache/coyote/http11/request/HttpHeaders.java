package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpHeaders {
    private final Map<String, String> headers;

    private HttpHeaders(final Map<String, String> headers) {
        this.headers = headers;
    }

    public static HttpHeaders create(final List<String> lines) {
        Map<String, String> headers = new HashMap<>();
        for (String line : lines) {
            String[] split = line.split(": ");
            headers.put(split[0], split[1]);
        }

        return new HttpHeaders(headers);
    }

    public String getHeader(String name) {
        return headers.get(name);
    }
}
