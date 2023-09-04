package org.apache.coyote.http11.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpHeaders {

    private static final String DELIMITER = ": ";
    private static final int HEADER_NAME_INDEX = 0;
    private static final int HEADER_VALUE_INDEX = 1;
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String EMPTY_LINE = "";

    private final Map<String, String> headers;

    private HttpHeaders(final Map<String, String> headers) {
        this.headers = headers;
    }

    public static HttpHeaders parse(final List<String> lines) {
        final Map<String, String> headers = new HashMap<>();

        for (final String line : lines) {
            final String[] splitLine = line.split(DELIMITER);

            final String key = splitLine[HEADER_NAME_INDEX];
            final String value = splitLine[HEADER_VALUE_INDEX];

            headers.put(key, value);
        }

        return new HttpHeaders(headers);
    }

    public static HttpHeaders createResponse(final byte[] bytes) {
        final Map<String, String> headers = new HashMap<>();

        headers.put(CONTENT_TYPE, "text/html;charset=utf-8");
        headers.put(CONTENT_LENGTH, String.valueOf(bytes.length));

        return new HttpHeaders(headers);
    }

    public boolean hasContentLength() {
        return headers.containsKey(CONTENT_LENGTH);
    }

    public int getContentLength() {
        return Integer.parseInt(headers.get(CONTENT_LENGTH));
    }

    @Override
    public String toString() {
        return String.join(" \r\n",
                CONTENT_TYPE + DELIMITER + headers.get(CONTENT_TYPE),
                CONTENT_LENGTH + DELIMITER + headers.get(CONTENT_LENGTH),
                EMPTY_LINE
                );
    }
}
