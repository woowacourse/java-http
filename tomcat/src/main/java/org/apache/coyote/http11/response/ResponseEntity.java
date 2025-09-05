package org.apache.coyote.http11.response;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public class ResponseEntity {

    private ResponseEntity() {
    }

    public static HttpResponse ok(String body) {
        return ok(body, "text/plain;charset=utf-8");
    }

    public static HttpResponse ok(String body, String contentType) {
        return buildHttpResponse(body, contentType, HttpStatus.OK);
    }

    public static HttpResponse notFound(String body) {
        return notFound(body, "text/plain;charset=utf-8");
    }

    public static HttpResponse notFound(String body, String contentType) {
        return buildHttpResponse(body, contentType, HttpStatus.NOT_FOUND);
    }

    public static HttpResponse badRequest(String body) {
        return badRequest(body, "text/plain;charset=utf-8");
    }

    public static HttpResponse badRequest(String body, String contentType) {
        return buildHttpResponse(body, contentType, HttpStatus.BAD_REQUEST);
    }

    public static HttpResponse unauthorized(String body) {
        return badRequest(body, "text/plain;charset=utf-8");
    }

    public static HttpResponse unauthorized(String body, String contentType) {
        return buildHttpResponse(body, contentType, HttpStatus.UNAUTHORIZED);
    }

    private static HttpResponse buildHttpResponse(String body, String contentType, HttpStatus status) {
        byte[] bodyBytes = body.getBytes(StandardCharsets.UTF_8);

        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Content-Type", contentType);
        headers.put("Content-Length", String.valueOf(bodyBytes.length));

        return new HttpResponse(
                new StatusLine(
                        String.join(" ",
                                "HTTP/1.1",
                                String.valueOf(status.getHttpStatusCode()),
                                status.getReasonPhrase()
                        )
                ),
                headers,
                body
        );
    }
}
