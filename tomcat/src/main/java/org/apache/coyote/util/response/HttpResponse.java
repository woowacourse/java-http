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

    public static HttpResponse of(String statusLine, String resourcePath) {
        String contentType = HttpContentTypeResolver.resolve(resourcePath);
        byte[] body = readStaticFile(resourcePath);
        return new HttpResponse(statusLine, contentType, body);
    }

    public static HttpResponse of(String statusLine, String contentType, byte[] body) {
        return new HttpResponse(statusLine, contentType, body);
    }

    private static byte[] readStaticFile(String path) {
        try (InputStream is = HttpResponse.class.getClassLoader().getResourceAsStream(path)) {
            if (is == null) {
                return "404 Not Found".getBytes();
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
        headers.put(key, value);
    }

    public void addCookie(String name, String value) {
        addHeader("Set-Cookie", name + "=" + value);
    }

    public byte[] getBody() {
        return body;
    }
}
