package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Headers {

    private final Map<String, String> headers;

    private Headers(Map<String, String> headers) {
        this.headers = headers;
    }

    public static Headers from(List<String> headers) {
        Map<String, String> headerMap = new HashMap<>();
        for (String header : headers) {
            String[] nameAndValue = header.split(": ");
            headerMap.put(nameAndValue[0], nameAndValue[1]);
        }
        return new Headers(headerMap);
    }

    public Map<String, String> headers() {
        return headers;
    }

    public boolean contains(String name) {
        return headers.containsKey(name);
    }

    public String get(String name) {
        return headers.getOrDefault(name, null);
    }
}
