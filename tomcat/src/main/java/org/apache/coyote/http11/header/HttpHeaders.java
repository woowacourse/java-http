package org.apache.coyote.http11.header;

import java.util.Map;

public class HttpHeaders {

    private static final String CONTENT_LENGTH_KEY = "Content-Length";

    private final Map<String, String> values;

    public HttpHeaders(final Map<String, String> values) {
        this.values = values;
    }

    public int getContentLength() {
        if (values.containsKey(CONTENT_LENGTH_KEY)) {
            return Integer.parseInt(values.get("Content-Length"));
        }
        return 0;
    }
}
