package org.apache.coyote.http11;

import java.util.Map;

public class Http11Headers {

    private final Map<String, String> headers;

    public Http11Headers(final Map<String, String> headers) {
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
}
