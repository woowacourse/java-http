package org.apache.coyote.http11.model.request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestHeader {

    public static final String COOKIE = "Cookie";
    private static final String CONTENT_LENGTH = "Content-Length";
    public static final int HEADER_KEY_INDEX = 0;
    public static final int HEADER_VALUE_INDEX = 1;
    private final Map<String, String> headers;

    public RequestHeader(final Map<String, String> headers) {
        this.headers = headers;
    }

    public static RequestHeader from(final List<String> input) {
        Map<String, String> headers = new HashMap<>();
        for (String s : input) {
            String[] split = s.split(" ");
            String key = split[HEADER_KEY_INDEX].substring(HEADER_KEY_INDEX, split[HEADER_KEY_INDEX]
                    .lastIndexOf(":"));
            headers.put(key, split[HEADER_VALUE_INDEX].trim());
        }
        return new RequestHeader(headers);
    }

    public int getContentLength() {
        return Integer.parseInt(headers.getOrDefault(CONTENT_LENGTH, "0"));
    }

    public boolean existHeader(String name) {
        return headers.containsKey(name);
    }

    public String getCookieKey() {
        return headers.get(COOKIE);
    }
}
