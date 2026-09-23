package org.apache.coyote.http11;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

public final class HttpResponse {

    private final HttpStatus status;
    private final HttpHeaders headers;
    private final ResponseContent content;

    private HttpResponse(
            final HttpStatus status,
            final HttpHeaders headers,
            final ResponseContent content
    ) {
        this.status = status;
        this.headers = headers;
        this.content = content;
    }

    public HttpStatus status() {
        return status;
    }

    HttpHeaders headers() {
        return headers;
    }

    public Optional<String> header(final String name) {
        return headers.firstValue(name);
    }

    ResponseContent content() {
        return content;
    }

    public HttpResponse addHeader(final String name, final String value) {
        return new HttpResponse(status, headers.add(name, value), content);
    }

    public static HttpResponse ok(final ResponseContent content) {
        return new HttpResponse(HttpStatus.OK, HttpHeaders.empty(), content);
    }

    public static HttpResponse redirect(final String location) {
        final var headers = HttpHeaders.empty()
                .add("Location", location);
        return new HttpResponse(HttpStatus.FOUND, headers, ResponseContent.empty());
    }

    public static HttpResponse error(final HttpStatus status) {
        final var body = status.reasonPhrase().getBytes(StandardCharsets.UTF_8);
        return new HttpResponse(
                status,
                HttpHeaders.empty(),
                new ResponseContent("text/plain;charset=utf-8", body));
    }
}
