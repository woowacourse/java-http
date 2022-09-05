package org.apache.coyote.http11;

import java.util.Map;
import java.util.stream.Collectors;

public class HttpResponse {

    public static final String PROTOCOL = "HTTP/1.1";
    private final HttpStatus httpStatus;
    private final Map<String, String> headers;
    private final String body;

    public HttpResponse(final HttpStatus httpStatus, final Map<String, String> headers, final String body) {
        this.httpStatus = httpStatus;
        this.headers = headers;
        this.body = body;
    }

    public String toResponseFormat() {
        String headersString = headers.entrySet()
                .stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .collect(Collectors.joining("\r\n"));
        return String.join("\r\n",
                PROTOCOL + " " + httpStatus.getValue() + " " + httpStatus.getMessage(),
                headersString,
                "",
                body
        );
    }
}
