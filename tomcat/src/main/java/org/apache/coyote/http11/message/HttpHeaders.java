package org.apache.coyote.http11.message;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public final class HttpHeaders {

    private static final String HEADER_LINE_DELIMITER = ": ";
    private static final int HEADER_NAME_INDEX = 0;
    private static final int HEADER_FIELD_INDEX = 1;

    private final Map<String, String> headers;

    public HttpHeaders(Map<String, String> headers) {
        this.headers = new HashMap<>(headers);
    }

    public static HttpHeaders from(List<String> headerLines) {
        return new HttpHeaders(headerLines.stream()
                .map(headerLine -> headerLine.split(HEADER_LINE_DELIMITER))
                .collect(Collectors.toMap(
                        headerLineElements -> headerLineElements[HEADER_NAME_INDEX],
                        headerLineElements -> headerLineElements[HEADER_FIELD_INDEX])));
    }

    public Optional<String> getFieldByHeaderName(String headerName) {
        return Optional.ofNullable(headers.get(headerName));
    }

    public List<String> toHeaderLines() {
        return headers.entrySet()
                .stream()
                .map(this::toHeaderLine)
                .toList();
    }

    private String toHeaderLine(Map.Entry<String, String> headersEntry) {
        return String.join(HEADER_LINE_DELIMITER, headersEntry.getKey(), headersEntry.getValue());
    }

    public void setHeader(String name, String field) {
        headers.put(name, field);
    }
}
