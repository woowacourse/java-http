package org.apache.coyote.http11.request.header;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpHeadersLine {
    private final Map<String , String> headers;

    private HttpHeadersLine(final Map<String , String> headers) {
        this.headers = headers;
    }

    public static HttpHeadersLine initHeaders() {
        return new HttpHeadersLine(new HashMap<>());
    }
    public static HttpHeadersLine from(final String[] httpHeadersLine) {
        return new HttpHeadersLine(Arrays.stream(httpHeadersLine)
                .map(header -> header.split(":"))
                .collect(Collectors.toMap(key -> key[0], value -> value[1])));
    }

    public static HttpHeadersLine from(final Map<String, String> httpHeaders) {
        return new HttpHeadersLine(httpHeaders);
    }

    public void put(final String key, final String value) {
        headers.put(key, value);
    }

    public int getContentLength() {
        return Integer.parseInt(headers.get("Content-Length").trim());
    }

    public boolean hasCookie(final String key) {
        return headers.containsKey("Cookie") && headers.get("Cookie").contains(key);
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}
