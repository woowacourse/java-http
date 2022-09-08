package org.apache.coyote.http11;

import java.util.stream.Collectors;

public class HttpResponse {

    private static final String PROTOCOL = "HTTP/1.1";
    private final HttpStatus httpStatus;
    private final HttpHeaders headers;
    private final String body;

    public HttpResponse(final HttpStatus httpStatus, final HttpHeaders headers, final String body) {
        this.httpStatus = httpStatus;
        this.headers = headers;
        this.body = body;
    }

    public String toResponseFormat() {
        String headersString = headers.getAll()
                .stream()
                .map(HttpHeader::toResponseFormat)
                .collect(Collectors.joining("\r\n"));
        String responseWithoutBody = String.join("\r\n",
                PROTOCOL + " " + httpStatus.getValue() + " " + httpStatus.getMessage(),
                headersString,
                ""
        );
        if (body.isBlank()) {
            return responseWithoutBody;
        }
        return responseWithoutBody + "\r\n" + body;
    }
}
