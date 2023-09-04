package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpRequestHeader {

    private final Map<String, String> headers;

    private HttpRequestHeader(final Map<String, String> headers) {
        this.headers = headers;
    }

    public static HttpRequestHeader from(final List<String> headers) {
        HashMap<String, String> httpHeaders = new HashMap<>();
        headers.stream()
                .forEach(header -> httpHeaders.put(header.split(": ")[0], header.split(": ")[1]));
        return new HttpRequestHeader(httpHeaders);
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getValue(String header) {
        return headers.get(header);
    }

    public boolean contains(String header) {
        return headers.containsKey(header);
    }
}
