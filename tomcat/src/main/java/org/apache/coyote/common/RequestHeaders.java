package org.apache.coyote.common;

import java.util.Map;

public class RequestHeaders {

    private static final String COOKIE_FIELD = "Cookie";
    private static final String CONTENT_LENGTH_FIELD = "Content-Length";

    private final Map<String, String> values;

    public RequestHeaders(Map<String, String> values) {
        this.values = values;
    }

    public Map<String, String> getValues() {
        return values;
    }

    public String getCookies() {
        return values.getOrDefault(COOKIE_FIELD, "");
    }

    public int getContentLength() {
        return Integer.parseInt(values.getOrDefault(CONTENT_LENGTH_FIELD, "0"));
    }

    @Override
    public String toString() {
        return "RequestHeaders{" +
               "values=" + values +
               '}';
    }
}
