package org.apache.coyote.http11;

import java.nio.charset.StandardCharsets;

public class Http11Response {

    private static final String CRLF = "\r\n";

    private final int statusCode;
    private final String statusMessage;
    private final String contentType; //헤더를 이거말고 더 필요하네,, 홀륑..
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

    public static Http11Response redirect(final String location) {
        return new Http11Response(302, "Found", "text/html;charset=utf-8", "") {
            @Override
            public byte[] toBytes() {
                String response = String.join(CRLF,
                        "HTTP/1.1 " + 302 + " " + "Found",
                        "Location: " + location,
                        "Content-Length: 0",
                        ""
                );
                return response.getBytes(StandardCharsets.UTF_8);
            }
        };
    }

    public static Http11Response redirect(final String location, final String cookieHeader) {
        return new Http11Response(302, "Found", "text/html;charset=utf-8", "") {
            @Override
            public byte[] toBytes() {
                String response = String.join(CRLF,
                        "HTTP/1.1 302 Found",
                        "Location: " + location,
                        "Set-Cookie: " + cookieHeader,
                        "Content-Length: 0",
                        ""
                );
                return response.getBytes(StandardCharsets.UTF_8);
            }
        };
    }

    public static Http11Response serverError() {
        final String body = "Internal Server Error";

        return new Http11Response(
                500,
                "Internal Server Error",
                "text/html;charset=utf-8",
                body
        );
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
