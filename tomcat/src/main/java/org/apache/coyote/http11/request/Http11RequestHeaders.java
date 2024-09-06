package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;

public class Http11RequestHeaders {

    private final Map<String, String> headerMap;

    public Http11RequestHeaders(Map<String, String> headerMap) {
        this.headerMap = headerMap;
    }

    public static Http11RequestHeaders from(String headerLines) {
        Map<String, String> map = new HashMap<>();

        for (String headerLine : headerLines.split(System.lineSeparator())) {
            String[] keyValue = headerLine.split(":");
            assert keyValue.length == 2;
            map.put(keyValue[0], keyValue[1]);
        }

        return new Http11RequestHeaders(map);
    }

    public String get(String key) {
        return headerMap.get(key);
    }

    public boolean contains(String key) {
        return headerMap.containsKey(key);
    }
}
