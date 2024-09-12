package org.apache.coyote.http11.request.header;

import static org.apache.coyote.http11.Constants.HTTP_HEADER_SEPARATOR;
import static org.apache.coyote.http11.Constants.NAME_INDEX;
import static org.apache.coyote.http11.Constants.VALID_PARAMETER_PAIR_LENGTH;
import static org.apache.coyote.http11.Constants.VALUE_INDEX;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class RequestHeaders {

    private final Map<String, String> headers;

    public RequestHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public RequestHeaders(List<String> headerFields) {
        this.headers = headerFields.stream()
                .map(field -> field.split(HTTP_HEADER_SEPARATOR))
                .filter(parts -> parts.length == VALID_PARAMETER_PAIR_LENGTH)
                .collect(Collectors.toMap(
                        parts -> parts[NAME_INDEX],
                        parts -> parts[VALUE_INDEX]
                ));
    }

    public Optional<String> get(String header) {
        return Optional.ofNullable(headers.get(header));
    }
}
