package org.apache.coyote.http;

import lombok.RequiredArgsConstructor;
import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
public class HttpResponse {

    private final String version;
    private final int statusCode;
    private final String contentType;
    private final String body;

    @Override
    public String toString() {
        return """
                HTTP/%s %d %s \r
                Content-Type: %s \r
                Content-Length: %d \r
                \r
                %s""".formatted(
                version, statusCode, statusCode == 200 ? "OK" : "TODO",
                contentType,
                body.getBytes(StandardCharsets.UTF_8).length,
                body);
    }
}
