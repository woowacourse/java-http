package org.apache.coyote.http11.model.request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestHeader {

    private static final String COOKIE = "Cookie";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final int HEADER_KEY_INDEX = 0;
    private static final int HEADER_VALUE_INDEX = 1;
    private final Map<String, String> headers;

    private RequestHeader(final Map<String, String> headers) {
        this.headers = headers;
    }

    public static RequestHeader from(final List<String> input) {
        Map<String, String> headers = new HashMap<>();
        for (String header : input) {
            String[] splitHeader = header.split(" ");
            String key = splitHeader[HEADER_KEY_INDEX].substring(HEADER_KEY_INDEX, splitHeader[HEADER_KEY_INDEX]
                    .lastIndexOf(":"));
            headers.put(key, splitHeader[HEADER_VALUE_INDEX].trim());
        }
        return new RequestHeader(headers);
    }

    public int getContentLength() {
        return Integer.parseInt(headers.getOrDefault(CONTENT_LENGTH, "0"));
    }

    public String getCookieKey() {
        return headers.get(COOKIE);
    }
}
