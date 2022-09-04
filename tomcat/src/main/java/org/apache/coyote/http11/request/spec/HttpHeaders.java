package org.apache.coyote.http11.request.spec;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpHeaders {

    private static final String HEADER_DELIMITER = ": ";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, String> headers = new HashMap<>();

    public HttpHeaders (List<String> headerLines) {
        for (String headerLine : headerLines) {
            String[] item = headerLine.split(HEADER_DELIMITER);
            String key = item[KEY_INDEX];
            String value = item[VALUE_INDEX];
            headers.put(key, value);
        }
    }
}
