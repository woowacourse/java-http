package org.apache.coyote.http11;

import java.nio.charset.StandardCharsets;

record HttpResponse(HttpStatus status, HttpHeaders headers, ResponseContent content) {

    HttpResponse addHeader(final String name, final String value) {
        return new HttpResponse(status, headers.add(name, value), content);
    }

    static HttpResponse ok(final ResponseContent content) {
        return new HttpResponse(HttpStatus.OK, HttpHeaders.empty(), content);
    }

    static HttpResponse redirect(final String location) {
        final var headers = HttpHeaders.empty()
                .add("Location", location);
        return new HttpResponse(HttpStatus.FOUND, headers, ResponseContent.empty());
    }

    static HttpResponse error(final HttpStatus status) {
        final var body = status.reasonPhrase().getBytes(StandardCharsets.UTF_8);
        return new HttpResponse(
                status,
                HttpHeaders.empty(),
                new ResponseContent("text/plain;charset=utf-8", body));
    }
}
