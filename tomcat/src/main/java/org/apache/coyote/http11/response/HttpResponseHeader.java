package org.apache.coyote.http11.response;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public record HttpResponseHeader(
        Map<String, String> headers
) {
    private static final String CRLF = "\r\n";
    private static final HttpResponseHeader EMPTY = new HttpResponseHeader(Map.of());

    public static HttpResponseHeader empty() {
        return EMPTY;
    }

    public HttpResponseHeader add(String key, String value) {
        Map<String, String> headers = new LinkedHashMap<>(this.headers);
        headers.put(key, value);
        return new HttpResponseHeader(headers);
    }

    public String format() {
        StringBuilder sb = new StringBuilder();
        headers.forEach((key, value) ->
                sb.append(key).append(": ").append(value).append(" ").append(CRLF));
        return sb.toString();
    }
}
