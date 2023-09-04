package org.apache.request;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toMap;

import java.util.List;
import java.util.Map;

public class HttpHeaders {

    private static final String HEADER_SPLIT_DELIMITER = ": ";
    private static final int HEADER_KEY_INDEX = 0;
    private static final int HEADER_VALUE_INDEX = 1;

    private final Map<String, String> headers;

    private HttpHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public static HttpHeaders of(List<String> lines) {
        return lines.stream()
                .map(line -> line.split(HEADER_SPLIT_DELIMITER))
                .collect(collectingAndThen(
                        toMap(line -> line[HEADER_KEY_INDEX], line -> line[HEADER_VALUE_INDEX]),
                        HttpHeaders::new)
                );
    }

    public boolean containsHeader(String header) {
        return headers.containsKey(header);
    }

    public String getHeaderValue(String headerKey) {
        if (!headers.containsKey(headerKey)) {
            throw new IllegalArgumentException("일치하는 키가 존재하지 않습니다.");
        }
        return headers.get(headerKey);
    }
}
