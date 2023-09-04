package org.apache.coyote.http11.request;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpRequestHeaders {

    private static final String HEADER_DELIMITER = ": ";
    private static final int HEADER_PARTS_COUNT = 2;
    private static final int HEADER_NAME_INDEX = 0;
    private static final int HEADER_VALUE_INDEX = 1;

    private final Map<String, String> headers;

    public HttpRequestHeaders(final List<String> header) {
        this.headers = parseHeader(header);
    }

    public static HttpRequestHeaders empty() {
        return new HttpRequestHeaders(Collections.emptyList());
    }

    private Map<String, String> parseHeader(final List<String> headerLines) {
        return headerLines.stream()
            .map(line -> line.split(HEADER_DELIMITER, HEADER_PARTS_COUNT))
            .filter(headerParts -> headerParts.length == HEADER_PARTS_COUNT)
            .collect(Collectors.toMap(
                headerParts -> headerParts[HEADER_NAME_INDEX].toLowerCase(),
                headerParts -> headerParts[HEADER_VALUE_INDEX],
                (prev, update) -> update)
            );
    }

    public boolean containsHeader(final String headerName) {
        return headers.containsKey(headerName);
    }

    public String getHeaderValue(final String headerName) {
        return headers.get(headerName.toLowerCase());
    }

    public Map<String, String> getHeaders() {
        return new HashMap<>(headers);
    }

    @Override
    public String toString() {
        return "HttpRequestHeaders{" +
            "headers=" + headers +
            '}';
    }
}
