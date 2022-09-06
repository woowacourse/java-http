package org.apache.coyote.http11.request;

import java.util.Map;

public class HttpCookie {
    private final Map<String, String> values;

    public HttpCookie(Map<String, String> values) {
        this.values = values;
    }
}
