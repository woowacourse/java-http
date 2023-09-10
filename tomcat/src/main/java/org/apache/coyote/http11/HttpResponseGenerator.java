package org.apache.coyote.http11;

import static java.util.stream.Collectors.joining;

import java.util.Map;

public class HttpResponseGenerator {

    private static final String EMPTY_VALUE = "";
    private static final String RESPONSE_LINE_DELIMITER = " ";
    private static final String HEADER_DELIMITER = ": ";
    private static final String END_SPACE = " ";

    public String generate(final HttpResponse response) {
        return String.join(System.lineSeparator(),
                makeResponseCode(response),
                makeResponseHeaders(response),
                EMPTY_VALUE,
                response.getResponseBody());
    }

    private String makeResponseCode(final HttpResponse response) {
        final String protocol = response.getProtocol();
        final int code = response.getStatusCode().getCode();
        final String message = response.getStatusCode().getMessage();
        return String.join(RESPONSE_LINE_DELIMITER, protocol, String.valueOf(code), message) + END_SPACE;
    }

    private String makeResponseHeaders(final HttpResponse response) {
        final Map<String, String> headers = response.getHeaders();
        return headers.entrySet()
                .stream()
                .map(entry -> entry.getKey() + HEADER_DELIMITER + entry.getValue() + END_SPACE)
                .collect(joining(System.lineSeparator()));
    }
}
