package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpHeader {

    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;
    private static final String HEADER_REGEX = ": ";
    private static final String LINE_FEED = "\n";

    private final Map<String, String> headers;

    public HttpHeader(String headers) {
        this.headers = parseHeaders(headers);
    }

    private Map<String, String> parseHeaders(String header) {
        Map<String, String> map = new HashMap<>();

        String[] headers = header.split(LINE_FEED);
        for (String value : headers) {
            String[] keyValue = value.split(HEADER_REGEX);
            map.put(keyValue[KEY_INDEX], keyValue[VALUE_INDEX]);
        }
        return map;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}
