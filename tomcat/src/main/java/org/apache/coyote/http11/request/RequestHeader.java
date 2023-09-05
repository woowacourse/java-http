package org.apache.coyote.http11.request;

import org.apache.coyote.http11.HttpCookie;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestHeader {

    private static final String COOKIE = "Cookie";
    private final Map<String, String> headers;

    public RequestHeader(final Map<String, String> headers) {
        this.headers = headers;
    }

    public static RequestHeader from(List<String> headerList) {
        final Map<String, String> map = new HashMap<>();
        for (String str : headerList) {
            final String[] parts = str.split(":");
            if (parts.length == 2) {
                final String key = parts[0].strip();
                final String value = parts[1].strip();
                map.put(key, value);
            }
        }
        return new RequestHeader(map);
    }

    public boolean hasJSessionId() {
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(COOKIE)) {
                return HttpCookie.from(entry.getValue()).hasJSessionId();
            }
        }
        return false;
    }

    public String getJSessionId() {
        final String cookie = headers.get(COOKIE);
        final String[] parts = cookie.split("=");
        return parts[1];
    }
}
