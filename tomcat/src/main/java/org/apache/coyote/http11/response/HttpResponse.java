package org.apache.coyote.http11.response;

import org.apache.coyote.http11.HttpStatus;

import java.util.Map;
import java.util.stream.Collectors;

public class HttpResponse {
    private static final String HTTP_PROTOCOL = "HTTP/1.1";
    private final HttpStatus httpStatus;
    private final Map<String,String> headers;
    private final String body;

    public HttpResponse(final HttpStatus httpStatus, final Map<String, String> headers, final String body) {
        this.httpStatus = httpStatus;
        this.headers = headers;
        this.body = body;
    }

    public String toResponse(){
        final String headersString = headers.entrySet()
                .stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .collect(Collectors.joining(System.lineSeparator()));

        final String responseWithoutBody = String.join(System.lineSeparator(),
                HTTP_PROTOCOL + " " + httpStatus.getCode() + " " + httpStatus.getMessage(),
                headersString,
                ""
        );

        if (body.isBlank()) {
            return responseWithoutBody;
        }

        return responseWithoutBody + System.lineSeparator() + body;
    }
}
