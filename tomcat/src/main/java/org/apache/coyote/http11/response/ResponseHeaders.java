package org.apache.coyote.http11.response;

import java.util.HashMap;
import java.util.Map;

public class ResponseHeaders {

    private final Map<String, String> headers;

    private ResponseHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public static ResponseHeaders initEmpty() {
        return new ResponseHeaders(new HashMap<>());
    }

    public ResponseHeaders addHeader(String key, String value) {
        headers.put(key, value);
        return new ResponseHeaders(headers);
    }

    public String headerToString(String key) {
        if ("Content-Type".equals(key)) {
            return String.format("Content-Type: text/%s;charset=utf-8 ", headers.get(key));
        }
        return String.format("%s: %s ", key, headers.get(key));
    }

    public boolean hasSetCookieHeader() {
        return headers.get("Set-Cookie") != null;
    }
}
