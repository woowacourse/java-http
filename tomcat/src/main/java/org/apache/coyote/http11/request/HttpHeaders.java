package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpHeaders {

    private static final String HEADER_DELIMITER = ": ";

    private final Map<String, String> headers = new HashMap<>();

    public HttpHeaders (List<String> headerLines) {
        for (String headerLine : headerLines) {
            String[] item = headerLine.split(HEADER_DELIMITER);
            String key = item[0];
            String value = item[1];
            headers.put(key, value);
        }
    }
}
