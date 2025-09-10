package org.apache.coyote.util.response;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class HttpResponse {

    private final String statusLine;
    private final String contentType;
    private final byte[] body;
    private final Map<String, String> headers = new HashMap<>();

    private HttpResponse(String statusLine, String contentType, byte[] body) {
        this.statusLine = statusLine;
        this.contentType = contentType;
        this.body = body;
    }

    public static HttpResponse of(String statusLine, String contentType, byte[] body) {
        return new HttpResponse(statusLine, contentType, body);
    }

    public static HttpResponse notFound() {
        byte[] body = readErrorFile("static/404.html");
        return new HttpResponse("HTTP/1.1 404 Not Found", "text/html;charset=utf-8", body);
    }

    public static HttpResponse internalServerError() {
        byte[] body = readErrorFile("static/500.html");
        return new HttpResponse("HTTP/1.1 500 Internal Server Error", "text/html;charset=utf-8", body);
    }

    public static HttpResponse unauthorized() {
        byte[] body = readErrorFile("static/401.html");
        return new HttpResponse("HTTP/1.1 401 Unauthorized", "text/html;charset=utf-8", body);
    }

    public static HttpResponse methodNotAllowed() {
        byte[] body = readErrorFile("static/405.html");
        return new HttpResponse("HTTP/1.1 405 Method Not Allowed", "text/html;charset=utf-8", body);
    }

    private static byte[] readErrorFile(String path) {
        try (InputStream is = HttpResponse.class.getClassLoader().getResourceAsStream(path)) {
            if (is == null) {
                if (path.contains("404")) {
                    return "404 Not Found".getBytes();
                }
                if (path.contains("401")) {
                    return "401 Unauthorized".getBytes();
                }
                return "500 Internal Server Error".getBytes();
            }
            return is.readAllBytes();
        } catch (IOException e) {
            return "500 Internal Server Error".getBytes();
        }
    }

    public String createHeader() {
        StringBuilder builder = new StringBuilder();
        builder.append(statusLine).append(" \r\n");
        builder.append("Content-Type: ").append(contentType).append(" \r\n");
        builder.append("Content-Length: ").append(body.length).append(" \r\n");
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            builder.append(entry.getKey()).append(": ").append(entry.getValue()).append(" \r\n");
        }
        builder.append("\r\n");
        return builder.toString();
    }

    public static HttpResponse redirect(String location) {
        HttpResponse response = new HttpResponse(
                "HTTP/1.1 302 Found ",
                "text/plain;charset=utf-8 ",
                "".getBytes()
        );
        response.addHeader("Location", location);
        return response;
    }

    public void addHeader(String key, String value) {
        String sanitizedValue = value.replaceAll("\\r|\\n", "");
        headers.put(key, sanitizedValue);
    }

    public void addCookie(String name, String value) {
        String cookieValue = String.format("%s=%s; Path=/; HttpOnly; SameSite=Lax", name, value);
        addHeader("Set-Cookie", cookieValue);
    }

    public byte[] getBody() {
        return body;
    }
}
