package org.apache.coyote.http11.request;

import static java.util.stream.Collectors.*;

import java.util.Arrays;
import java.util.List;

class RequestLine {

    private static final String REQUEST_LINE_DELIMITER = " ";

    private final String method;
    private final String requestUri;
    private final String version;

    RequestLine(final HttpMessage message) {
        final List<String> values = parseRequestLine(message.getRequestLine());
        this.method = values.get(0);
        this.requestUri = values.get(1);
        this.version = values.get(2);
    }

    private List<String> parseRequestLine(final String requestLine) {
        return Arrays.stream(requestLine.split(REQUEST_LINE_DELIMITER))
            .map(String::trim)
            .collect(toList());
    }

    boolean isGet() {
        return method.equals("GET");
    }

    String getVersion() {
        return version;
    }

    String getUri() {
        return requestUri;
    }
}
