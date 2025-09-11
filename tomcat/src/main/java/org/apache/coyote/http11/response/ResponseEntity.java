package org.apache.coyote.http11.response;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public class ResponseEntity {

    private ResponseEntity() {
    }

    public static HttpResponse ok(String body) {
        return ok(body, MimeType.TXT);
    }

    public static HttpResponse ok(String body, MimeType contentType) {
        return buildHttpResponse(body, contentType, HttpStatus.OK);
    }

    public static HttpResponse found(String body) {
        return found(body, MimeType.TXT, "/index.html");
    }

    public static HttpResponse found(String body, MimeType contentType, String redirectPath) {
        HttpResponse response = buildHttpResponse(body, contentType, HttpStatus.FOUND);
        response.setHeader("Location", redirectPath);
        return response;
    }

    public static HttpResponse badRequest(String body) {
        return badRequest(body, MimeType.TXT);
    }

    public static HttpResponse badRequest(String body, MimeType contentType) {
        return buildHttpResponse(body, contentType, HttpStatus.BAD_REQUEST);
    }

    public static HttpResponse unauthorized(String body) {
        return unauthorized(body, MimeType.TXT);
    }

    public static HttpResponse unauthorized(String body, MimeType contentType) {
        return buildHttpResponse(body, contentType, HttpStatus.UNAUTHORIZED);
    }

    public static HttpResponse notFound(String body) {
        return notFound(body, MimeType.TXT);
    }

    public static HttpResponse notFound(String body, MimeType contentType) {
        return buildHttpResponse(body, contentType, HttpStatus.NOT_FOUND);
    }

    public static HttpResponse conflict(String body) {
        return conflict(body, MimeType.TXT);
    }

    public static HttpResponse conflict(String body, MimeType contentType) {
        return buildHttpResponse(body, contentType, HttpStatus.CONFLICT);
    }

    public static HttpResponse internalServerError(String body) {
        return internalServerError(body, MimeType.TXT);
    }

    public static HttpResponse internalServerError(String body, MimeType contentType) {
        return buildHttpResponse(body, contentType, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private static HttpResponse buildHttpResponse(String body, MimeType contentType, HttpStatus status) {
        byte[] bodyBytes = body.getBytes(StandardCharsets.UTF_8);

        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Content-Type", contentType.getValue());
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
