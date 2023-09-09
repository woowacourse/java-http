package org.apache.coyote.publisher;

import org.apache.coyote.common.Headers;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RequestHeaderPublisher {

    private static final String HEADER_DELIMITER = ":";
    private static final int HEADER_NAME_INDEX = 0;
    private static final int HEADER_VALUE_INDEX = 1;

    private final Map<String, String> headers;

    private RequestHeaderPublisher(final Map<String, String> headers) {
        this.headers = headers;
    }

    public static RequestHeaderPublisher read(final List<String> headerNamesAndValues) {
        final Map<String, String> headerMapping = headerNamesAndValues.stream()
                .map(headerWithValue -> headerWithValue.split(HEADER_DELIMITER))
                .collect(Collectors.toMap(
                        entry -> entry[HEADER_NAME_INDEX].strip(),
                        entry -> entry[HEADER_VALUE_INDEX].strip()
                ));

        return new RequestHeaderPublisher(headerMapping);
    }

    public Headers toHeaders() {
        final Headers newHeaders = Headers.empty();
        headers.entrySet().forEach(entry -> {
            newHeaders.addHeader(entry.getKey(), entry.getValue());
        });

        return newHeaders;
    }
}
