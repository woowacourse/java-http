package org.apache.coyote.http11.response;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public class ResponseEntity {

    private static final String DEFAULT_CONTENT_TYPE = "text/plain;charset=utf-8";
    private final HttpResponse httpResponse;

    private ResponseEntity(HttpResponse httpResponse) {
        this.httpResponse = httpResponse;
    }

    public static ResponseEntity ok() {
        return buildReponse(null, DEFAULT_CONTENT_TYPE);
    }

    public static ResponseEntity ok(String body) {
        return buildReponse(body, DEFAULT_CONTENT_TYPE);
    }

    public static ResponseEntity ok(String body, String contentType) {
        return buildReponse(body, contentType);
    }

    private static ResponseEntity buildReponse(String body, String contentType) {
        if (body == null) {
            body = "";
        }

        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        Map<String, String> headers = Map.of(
                "Content-Type", contentType,
                "Content-Length", String.valueOf(bytes.length)
        );

        return new ResponseEntity(new HttpResponse(headers, body));
    }
}
