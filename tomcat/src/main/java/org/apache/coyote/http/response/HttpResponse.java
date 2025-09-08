package org.apache.coyote.http.response;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public class HttpResponse {

    private final int statusCode;
    private final Map<String, String> headers;
    private final String body;

    private HttpResponse(int statusCode, Map<String, String> headers, String body) {
        this.statusCode = statusCode;
        this.headers = headers;
        this.body = body;
    }

    public static HttpResponse ok(String body, String contentType) {
        Map<String, String> headers = Map.of(
                "Content-Type", contentType + ";charset=utf-8",
                "Content-Length", String.valueOf(body.getBytes().length)
        );
        return new HttpResponse(200, headers, body);
    }

    public static HttpResponse redirect(String location) {
        return new HttpResponse(302, Map.of("Location", location), "");
    }

    public static HttpResponse unauthorized(String body) {
        Map<String, String> headers = Map.of(
                "Content-Type", "text/html;charset=utf-8",
                "Content-Length", String.valueOf(body.getBytes().length)
        );
        return new HttpResponse(401, headers, body);
    }

    public void writeTo(OutputStream outputStream) throws IOException {
        StringBuilder response = new StringBuilder();
        response.append("HTTP/1.1 ").append(statusCode).append(" ").append(getStatusText()).append("\r\n");

        for (Map.Entry<String, String> header : headers.entrySet()) {
            response.append(header.getKey()).append(": ").append(header.getValue()).append("\r\n");
        }
        response.append("\r\n");
        response.append(body);

        outputStream.write(response.toString().getBytes());
        outputStream.flush();
    }

    private String getStatusText() {
        return switch (statusCode) {
            case 200 -> "OK";
            case 302 -> "Found";
            case 401 -> "Unauthorized";
            default -> "Unknown";
        };
    }
}
