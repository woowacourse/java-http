package org.apache.coyote.http11.request;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RequestHeaders {

    private static final String HEADER_DELIMITER = ": ";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, String> headers;

    public RequestHeaders(List<String> headers) {
        this.headers = headers.stream()
                .map(header -> header.split(HEADER_DELIMITER))
                .collect(Collectors.toMap(parts -> parts[KEY_INDEX], parts -> parts[VALUE_INDEX]));
    }

    public String findHeader(final String name) {
        return headers.getOrDefault(name, name);
    }

    public int getContentLength() {
        return Integer.parseInt(headers.get("Content-Length"));
    }
}
