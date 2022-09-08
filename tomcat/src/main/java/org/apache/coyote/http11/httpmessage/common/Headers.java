package org.apache.coyote.http11.httpmessage.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Headers {

    private final static int HEADER_NAME = 0;
    private final static int HEADER_VALUE = 0;

    private final static String HEADER_SPLITTER = ": ";
    private final static String BLANK = " ";
    private final static String DEFAULT_CONTENT_LENGTH = "0";


    final Map<String, String> headers;

    public Headers() {
        this.headers = new LinkedHashMap<>();
    }

    public Headers(final Map<String, String> headers) {
        this.headers = headers;
    }

    public Headers(final BufferedReader bufferedReader) throws IOException {
        headers = new HashMap<>();
        while (true) {
            String headerLine = bufferedReader.readLine();
            if (headerLine.equals("\r\n") || "".equals(headerLine)) {
                break;
            }
            String key = headerLine.split(HEADER_SPLITTER)[HEADER_NAME];
            String value = headerLine.split(HEADER_SPLITTER)[HEADER_VALUE];
            headers.put(key, value);
        }
    }

    public Headers add(final String fieldName, final String fieldValue) {
        final Map<String, String> newHeaders = new LinkedHashMap<>(headers);
        newHeaders.put(fieldName, fieldValue);

        return new Headers(newHeaders);
    }

    public int getContentLength() {
        return Integer.parseInt(headers.getOrDefault("Content-Length", DEFAULT_CONTENT_LENGTH));
    }

    public boolean exist(final String headerName) {
        return headers.containsKey(headerName);
    }

    public String getValue(final String headerName) {
        return headers.get(headerName);
    }

    public String parseToString() {
        return headers.entrySet()
                .stream()
                .map(it -> it.getKey() + HEADER_SPLITTER + it.getValue() + BLANK)
                .collect(Collectors.joining("\r\n"));
    }
}
