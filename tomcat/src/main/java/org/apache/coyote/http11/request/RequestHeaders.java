package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestHeaders {

    private static final String DELIMITER = ": ";
    private static final int HEADER_NAME_INDEX = 0;
    private static final int HEADER_VALUE_INDEX = 1;
    private static final String CONTENT_LENGTH = "Content-Length";

    private final Map<String, String> headers;

    private RequestHeaders(final Map<String, String> headers) {
        this.headers = headers;
    }

    public static RequestHeaders parse(final List<String> lines) {
        final Map<String, String> headers = new HashMap<>();

        for (final String line : lines) {
            final String[] splitLine = line.split(DELIMITER);

            final String key = splitLine[HEADER_NAME_INDEX];
            final String value = splitLine[HEADER_VALUE_INDEX];

            headers.put(key, value);
        }

        return new RequestHeaders(headers);
    }

    public boolean hasContentLength() {
        return headers.containsKey(CONTENT_LENGTH);
    }

    public int getContentLength() {
        return Integer.parseInt(headers.get(CONTENT_LENGTH));
    }
}
