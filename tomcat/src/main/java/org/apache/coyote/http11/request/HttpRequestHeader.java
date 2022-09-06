package org.apache.coyote.http11.request;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HttpRequestHeader {

    private static final String HTTP_REQUEST_HEADER_KEY_VALUE_DELIMITER = ": ";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, String> values;

    public HttpRequestHeader(Map<String, String> values) {
        this.values = values;
    }

    public static HttpRequestHeader from(List<String> requestHeaderLines) {
        Map<String, String> headers = new ConcurrentHashMap<>();
        for (String line : requestHeaderLines) {
            String[] keyAndValue = line.split(HTTP_REQUEST_HEADER_KEY_VALUE_DELIMITER);
            headers.put(keyAndValue[KEY_INDEX], keyAndValue[VALUE_INDEX]);
        }
        return new HttpRequestHeader(headers);
    }

    public boolean hasContentLength() {
        return values.keySet()
                .stream()
                .anyMatch("Content-Length"::equals);
    }

    public String getValueOf(String key) {
        return values.get(key);
    }
}
