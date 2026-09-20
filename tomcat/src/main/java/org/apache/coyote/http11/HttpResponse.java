package org.apache.coyote.http11;

import java.nio.charset.StandardCharsets;

public class HttpResponse {

    private final int statusCode;
    private final String statusText;
    private final String contentType;
    private final byte[] body;

    public HttpResponse(
            int statusCode,
            String statusText,
            String contentType,
            byte[] body
    ) {
        this.statusCode = statusCode;
        this.statusText = statusText;
        this.contentType = contentType;
        this.body = body;
    }

    public static HttpResponse ok(String contentType, byte[] body) {
        return new HttpResponse(
                200,
                "OK",
                contentType,
                body
        );
    }

    public static HttpResponse notFound(byte[] body) {
        return new HttpResponse(
                404,
                "NOT FOUND",
                "text/html;charset=utf-8",
                body
        );
    }

    public static HttpResponse unauthorized(byte[] body) {
        return new HttpResponse(
                401,
                "UNAUTHORIZED",
                "text/html;charset=utf-8",
                body
        );
    }

    @Override
    public String toString() {
        return String.join(
                "\r\n",
                "HTTP/1.1 " + statusCode + " " + statusText + " ",
                "Content-Type: " + contentType + " ",
                "Content-Length: " + body.length + " ",
                "",
                new String(body, StandardCharsets.UTF_8)
        );
    }
}
