package org.apache.coyote.http11.request;

import java.util.Map;

public class HttpRequestHeaders {

    private static final String CONTENT_LENGTH_KEY = "Content-Length";

    private final Map<String, String> values;

    public HttpRequestHeaders(final Map<String, String> values) {
        this.values = values;
    }

    public int getContentLength() {
        if (values.containsKey(CONTENT_LENGTH_KEY)) {
            return Integer.parseInt(values.get(CONTENT_LENGTH_KEY));
        }
        return 0;
    }

    public String getCookie() {
        return values.get("Cookie");
    }
}
