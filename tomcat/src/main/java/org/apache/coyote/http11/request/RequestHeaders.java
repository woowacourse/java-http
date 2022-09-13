package org.apache.coyote.http11.request;

import static java.util.stream.Collectors.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class RequestHeaders {

    private static final String LINE_BREAK = "\r\n";
    private static final String HEADER_DELIMITER = ":";

    private final Map<String, String> headers = new HashMap<>();

    RequestHeaders(final HttpMessage message) {
        parseHeaders(message.getHeaders());
    }

    private void parseHeaders(final String messageHeaders) {
        for (final String header : messageHeaders.split(LINE_BREAK)) {
            putHeader(header);
        }
    }

    private void putHeader(final String headerLine) {
        final List<String> headerAndValue = parseHeaderLine(headerLine);
        headers.put(headerAndValue.get(0), headerAndValue.get(1));
    }

    private List<String> parseHeaderLine(final String headerLine) {
        return Arrays.stream(headerLine.split(HEADER_DELIMITER))
            .map(String::trim)
            .collect(toList());
    }

    boolean contains(String headerName) {
        return headers.containsKey(headerName);
    }

    String getHeaderValue(String headerName) {
        return headers.get(headerName);
    }
}
