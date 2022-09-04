package org.apache.coyote.http;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpHeader {

    private static final String HEADER_SEPARATOR = ":";
    private static final int SPLIT_SIZE = 2;
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private Map<String, String> values;

    private HttpHeader(final Map<String, String> headers) {
        this.values = headers;
    }

    public static HttpHeader from(final List<String> headerList) {
        Map<String, String> headers = new HashMap<>();
        for (String header : headerList) {
            String[] headerLine = header.split(HEADER_SEPARATOR, SPLIT_SIZE);
            headers.put(headerLine[KEY_INDEX], headerLine[VALUE_INDEX]);
        }
        return new HttpHeader(headers);
    }
}
