package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestHeaders {

    private final Map<String, String> headers;

    private RequestHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public static RequestHeaders from(List<String> headers) {
        Map<String, String> headerPairs = new HashMap<>();
        for (String header : headers) {
            String[] headerProperties = header.split(": ");
            headerPairs.put(headerProperties[0], headerProperties[1]);
        }
        return new RequestHeaders(headerPairs);
    }

    public int getContentLength() {
        if (headers.containsKey("Content-Length")) {
            return Integer.parseInt(headers.get("Content-Length"));
        }
        return 0;
    }

    public boolean isExistCookie() {
        return headers.containsKey("Cookie");
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    @Override
    public String toString() {
        return "RequestHeaders{" +
                "headers=" + headers +
                '}';
    }
}
