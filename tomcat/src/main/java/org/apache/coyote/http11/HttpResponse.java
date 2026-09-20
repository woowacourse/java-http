package org.apache.coyote.http11;

import java.nio.charset.StandardCharsets;

record HttpResponse(HttpStatus status, ResponseContent content) {

    static HttpResponse ok(final ResponseContent content) {
        return new HttpResponse(HttpStatus.OK, content);
    }

    static HttpResponse error(final HttpStatus status) {
        final var body = status.reasonPhrase().getBytes(StandardCharsets.UTF_8);
        return new HttpResponse(status, new ResponseContent("text/plain;charset=utf-8", body));
    }
}
