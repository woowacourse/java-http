package org.apache.coyote.http11.request;

import static java.util.stream.Collectors.*;

import java.util.Arrays;
import java.util.List;

public class RequestLine {

    private static final String REQUEST_LINE_DELIMITER = " ";

    private final String method;
    private final String requestUri;
    private final String version;

    public RequestLine(String message) {
        List<String> values = parseRequestLine(message);
        this.method = values.get(0);
        this.requestUri = values.get(1);
        this.version = values.get(2);
    }

    private List<String> parseRequestLine(final String message) {
        final String requestLine = message.split("\r\n")[0];
        return Arrays.stream(requestLine.split(REQUEST_LINE_DELIMITER))
            .map(String::trim)
            .collect(toList());
    }

    public boolean isGet() {
        return method.equals("GET");
    }

    public String getVersion() {
        return version;
    }

    public String getUri() {
        return requestUri;
    }
}
