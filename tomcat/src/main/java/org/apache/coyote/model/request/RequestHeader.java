package org.apache.coyote.model.request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestHeader {

    private static final String CONTENT_LENGTH = "Content-Length";
    public static final String COOKIE = "Cookie";
    private final Map<String, String> requestHeader;

    private RequestHeader(Map<String, String> requestHeader) {
        this.requestHeader = requestHeader;
    }

    public static RequestHeader of(List<String> input) {
        Map<String, String> requestHeader = new HashMap<>();
        for (String header : input) {
            String[] contents = header.split(" ");
            String key = contents[0].substring(0, contents[0].lastIndexOf(":"));
            String value = contents[1].trim();
            requestHeader.put(key, value);
        }
        return new RequestHeader(requestHeader);
    }

    public int getContentLength() {
        return Integer.parseInt(requestHeader.getOrDefault(CONTENT_LENGTH, "0"));
    }

    public boolean existKey(String key) {
        return requestHeader.containsKey(key);
    }

    public Map<String, String> getRequestHeader() {
        return requestHeader;
    }

    public String getCookieKey() {
        return requestHeader.get(COOKIE);
    }
}
