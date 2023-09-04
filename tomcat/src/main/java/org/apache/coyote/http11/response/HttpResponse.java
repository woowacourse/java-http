package org.apache.coyote.http11.response;

import java.util.Collections;
import java.util.List;

public class HttpResponse {

    private final String statusLine;
    private final List<String> headers;
    private final String body;

    public HttpResponse(final String statusLine, final String body, final List<String> headers) {
        this.statusLine = statusLine;
        this.body = body;
        this.headers = headers;
    }

    public static HttpResponse withBody(
        final String statusLine,
        final String contentType,
        final String body
    ) {
        final List<String> headers = List.of(
            String.format("Content-Type: %s ", contentType),
            String.format("Content-Length: %d ", body.getBytes().length)
        );

        return new HttpResponse(statusLine, body, headers);
    }

    public static HttpResponse ok(final String contentType, final String body) {
        return withBody("HTTP/1.1 200 OK ", contentType, body);
    }

    public static HttpResponse notFound() {
        return new HttpResponse("HTTP/1.1 404 Not Found", "", Collections.emptyList());
    }

    public String convertToMessage() {
        final StringBuilder message = new StringBuilder();

        message.append(statusLine).append(System.lineSeparator());
        headers.forEach(header -> message.append(header).append(System.lineSeparator()));
        message.append(System.lineSeparator());
        message.append(body);

        return message.toString();
    }
}

