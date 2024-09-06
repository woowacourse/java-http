package org.apache.coyote.request;

import java.util.List;
import java.util.Map;

public class HttpRequestHeader {

    private static final String CONTENT_LENGTH = "Content-Length";
    private final Map<String, List<String>> headers;

    private HttpRequestHeader(Map<String, List<String>> headers) {
        this.headers = headers;
    }

    public static HttpRequestHeader from(Map<String, List<String>> headers) {
        Map<String, List<String>> values = Map.copyOf(headers);
        return new HttpRequestHeader(values);
    }

    public int getContentLength() {
        if (headers.containsKey(CONTENT_LENGTH)) {
            return Integer.parseInt(headers.get(CONTENT_LENGTH).getFirst());
        }
        return 0;
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    @Override
    public String toString() {
        return "HttpRequestHeader{" +
                "headers=" + headers +
                '}';
    }
}
