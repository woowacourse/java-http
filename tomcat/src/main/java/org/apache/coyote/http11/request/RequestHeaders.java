package org.apache.coyote.http11.request;

import java.util.Arrays;
import java.util.Map;
import org.apache.coyote.exception.JsessionidNotFoundException;

public class RequestHeaders {

    private final Map<String, String> headers;

    public RequestHeaders(final Map<String, String> headers) {
        this.headers = headers;
    }

    public void add(final String key, final String value) {
        headers.put(key, value);
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public int getContentLength() {
        final String contentLength = headers.get("Content-Length");
        if (contentLength != null) {
            return Integer.parseInt(contentLength.trim());
        }
        return 0;
    }

    public boolean hasJsessionid() {
        final String cookie = headers.get("Cookie");
        if (cookie == null) {
            return false;
        }
        return Arrays.stream(cookie.split("; "))
                .anyMatch(tuple -> tuple.contains("JSESSIONID="));
    }

    public String getJsessionid() {
        final String cookie = headers.get("Cookie");
        if (cookie == null) {
            return "";
        }
        return Arrays.stream(cookie.split("; "))
                .filter(tuple -> tuple.contains("JSESSIONID="))
                .findFirst()
                .orElseThrow(JsessionidNotFoundException::new);
    }
}
