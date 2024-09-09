package org.apache.coyote.http11.request.header;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class RequestHeaders {

    private static final String HTTP_HEADER_SEPARATOR = ": ";

    private static final int NAME_INDEX = 0;
    private static final int VALUE_INDEX = 1;
    private static final int VALID_HEADER_PAIR_LENGTH = 2;

    private final Map<String, String> headers;

    public RequestHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public RequestHeaders(List<String> headerFields) {
        this.headers = headerFields.stream()
                .map(field -> field.split(HTTP_HEADER_SEPARATOR))
                .filter(parts -> parts.length == VALID_HEADER_PAIR_LENGTH)
                .collect(Collectors.toMap(
                        parts -> parts[NAME_INDEX],
                        parts -> parts[VALUE_INDEX]
                ));
    }

    public Optional<String> get(String header) {
        return Optional.ofNullable(headers.get(header));
    }
}
