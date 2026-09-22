package org.apache.coyote.http11;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public final class HttpResponse {

    private final int statusCode;
    private final String reasonPhrase;
    private final String location;
    private final String contentType;
    private final byte[] body;

    private HttpResponse(int statusCode, String reasonPhrase, String location, String contentType, byte[] body) {
        this.statusCode = statusCode;
        this.reasonPhrase = reasonPhrase;
        this.location = location;
        this.contentType = contentType;
        this.body = body;
    }

    public static HttpResponse ok(String contentType, byte[] body) {
        return new HttpResponse(200, "OK", "", contentType, body);
    }

    public static HttpResponse found(String location, String contentType, byte[] body) {
        return new HttpResponse(302, "Found", location, contentType, body);
    }

    public static HttpResponse notFound(String contentType, byte[] body) {
        return new HttpResponse(404, "Not Found", "", contentType, body);
    }

    public void writeTo(OutputStream outputStream) throws IOException {
        StringBuilder headers = new StringBuilder();
        headers.append("HTTP/1.1 ")
                .append(statusCode).append(" ").append(reasonPhrase).append(" \r\n");

        if (!location.isBlank()) {
            headers.append("Location: ").append(location).append("\r\n");
        }
        if (!contentType.isBlank()) {
            headers.append("Content-Type: ").append(contentType).append(";charset=utf-8 \r\n");
        }
        headers.append("Content-Length: ").append(body.length).append(" \r\n")
                .append("\r\n");

        outputStream.write(headers.toString().getBytes(StandardCharsets.UTF_8));
        outputStream.write(body);
    }
}
