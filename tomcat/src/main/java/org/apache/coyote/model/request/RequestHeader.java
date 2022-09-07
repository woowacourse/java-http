package org.apache.coyote.model.request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestHeader {

    private static final String CONTENT_LENGTH = "Content-Length";
    public static final String COOKIE = "Cookie";
    public static final int KEY = 0;
    public static final int VALUE = 1;
    private final Map<String, String> requestHeader;

    private RequestHeader(final Map<String, String> requestHeader) {
        this.requestHeader = requestHeader;
    }

    public static RequestHeader of(final List<String> input) {
        final Map<String, String> requestHeader = new HashMap<>();
        for (String header : input) {
            final String[] contents = header.split(" ");
            final String key = contents[KEY].substring(KEY, contents[KEY].lastIndexOf(":"));
            final String value = contents[VALUE].trim();
            requestHeader.put(key, value);
        }
        return new RequestHeader(requestHeader);
    }

    public int getContentLength() {
        return Integer.parseInt(requestHeader.getOrDefault(CONTENT_LENGTH, "0"));
    }

    public boolean existKey(final String key) {
        return requestHeader.containsKey(key);
    }

    public Map<String, String> getRequestHeader() {
        return requestHeader;
    }

    public String getCookieKey() {
        return requestHeader.get(COOKIE);
    }
}
