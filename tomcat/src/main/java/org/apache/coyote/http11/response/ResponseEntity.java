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
        byte[] bodyBytes = body.getBytes(StandardCharsets.UTF_8);

        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Content-Type", contentType);
        headers.put("Content-Length", String.valueOf(bodyBytes.length));

        return new HttpResponse(
                new StatusLine(
                        String.join(" ",
                                "HTTP/1.1",
                                String.valueOf(HttpStatus.OK.getHttpStatusCode()),
                                HttpStatus.OK.getReasonPhrase()
                        )
                ),
                headers,
                body
        );
    }

    public static HttpResponse notFound(String body) {
        return notFound(body, "text/plain;charset=utf-8");
    }

    public static HttpResponse notFound(String body, String contentType) {
        byte[] bodyBytes = body.getBytes(StandardCharsets.UTF_8);

        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Content-Type", contentType);
        headers.put("Content-Length", String.valueOf(bodyBytes.length));

        return new HttpResponse(
                new StatusLine(
                        String.join(" ",
                                "HTTP/1.1",
                                String.valueOf(HttpStatus.NOT_FOUND.getHttpStatusCode()),
                                HttpStatus.NOT_FOUND.getReasonPhrase()
                        )
                ),
                headers,
                body
        );
    }
}
