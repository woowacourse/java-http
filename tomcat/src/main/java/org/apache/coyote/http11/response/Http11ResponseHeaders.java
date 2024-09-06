package org.apache.coyote.http11.response;

import java.util.HashMap;
import java.util.Map;

public class Http11ResponseHeaders {

    private final Map<String, String> headerMap;

    public Http11ResponseHeaders(Map<String, String> headerMap) {
        this.headerMap = headerMap;
    }

    public static Http11ResponseHeaders from(String headerLines) {
        Map<String, String> map = new HashMap<>();

        for (String headerLine : headerLines.split("\r\n")) {
            String[] keyValue = headerLine.split(":");
            assert keyValue.length == 2;
            map.put(keyValue[0], keyValue[1]);
        }

        return new Http11ResponseHeaders(map);
    }

    public static Http11ResponseHeaders instance() {
        return new Http11ResponseHeaders(new HashMap<>());
    }

    public void addHeader(String key, String value) {
        headerMap.put(key, value);
    }

    public String asString() {
        final StringBuilder sb = new StringBuilder();
        headerMap.forEach((key, value) -> sb.append(key).append(":").append(value).append("\r\n"));
        return sb.toString();
    }
}
