package org.apache.coyote.http11.model.request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestHeader {

    public static final String SET_COOKIE = "Set-Cookie";
    private static final String CONTENT_LENGTH = "Content-Length";
    private final Map<String, String> headers;

    public RequestHeader(final Map<String, String> headers) {
        this.headers = headers;
    }

    public static RequestHeader from(final List<String> input) {
        Map<String, String> headers = new HashMap<>();
        for (String s : input) {
            String[] split = s.split(" ");
            String key = split[0].substring(0, split[0].lastIndexOf(":"));
            headers.put(key, split[1].trim());
        }
        return new RequestHeader(headers);
    }

    public int getContentLength() {
        return Integer.parseInt(headers.getOrDefault(CONTENT_LENGTH, "0"));
    }

    public boolean existHeader(String name) {
        return headers.containsKey(name);
    }
}
