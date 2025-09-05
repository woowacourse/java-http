package org.apache.coyote.http11;

import java.nio.charset.StandardCharsets;

public class Http11Response {

    private static final String CRLF = "\r\n";

    private final int statusCode;
    private final String statusMessage;
    private final String contentType;
    private final String responseBody;

    private Http11Response(final int statusCode, final String statusMessage, final String contentType, final String responseBody) {
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
        this.contentType = contentType;
        this.responseBody = responseBody;
    }

    public static Http11Response ok(final String contentType, final String body) {
        return new Http11Response(200, "OK", contentType, body);
    }

    public static Http11Response notFound(final String contentType, final String responseBody) {
        return new Http11Response(404, "Not Found", contentType, responseBody);
    }

    public byte[] toBytes() {
        String response = String.join(CRLF,
                "HTTP/1.1 " + statusCode + " " + statusMessage + " ",
                "Content-Type: " + contentType + " ",
                "Content-Length: " + responseBody.getBytes(StandardCharsets.UTF_8).length + " ",
                "",
                responseBody
        );

        return response.getBytes(StandardCharsets.UTF_8);
    }
}
