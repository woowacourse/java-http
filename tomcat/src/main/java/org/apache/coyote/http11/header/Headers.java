package org.apache.coyote.http11.header;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Headers {

    private static final String HEADER_DELIMITER = ":";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, String> headers;

    public Headers() {
        this.headers = new LinkedHashMap<>();
    }

    public Headers(List<String> headers) {
        this.headers = toMap(headers);
    }

    private Map<String, String> toMap(List<String> headerLines) {
        Map<String, String> headers = new LinkedHashMap<>();

        for (String headerLine : headerLines) {
            String[] headerInfo = headerLine.split(HEADER_DELIMITER);
            headers.put(headerInfo[KEY_INDEX].trim(), headerInfo[VALUE_INDEX].trim());
        }

        return headers;
    }

    public boolean has(HttpHeader httpHeader) {
        return headers.containsKey(httpHeader.getName());
    }

    public String get(HttpHeader httpHeader) {
        return headers.get(httpHeader.getName());
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}
