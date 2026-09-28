package org.apache.coyote.http11;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpResponse {

    private final OutputStream outputStream;
    private final Map<String, String> headers = new LinkedHashMap<>();
    private String status = "200 OK";
    private String contentType = "text/html;charset=utf-8";
    private byte[] body = new byte[0];

    public HttpResponse(final OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void addHeader(final String name, final String value) {
        headers.put(name, value);
    }

    public void body(final String body) {
        body(body.getBytes(StandardCharsets.UTF_8), "text/html;charset=utf-8");
    }

    public void body(final byte[] body, final String contentType) {
        this.body = body;
        this.contentType = contentType;
    }

    public void sendRedirect(final String location) {
        status = "302 Found";
        addHeader("Location", location);
        body = new byte[0];
    }

    public void sendError(final int statusCode, final String reasonPhrase) {
        status = statusCode + " " + reasonPhrase;
        headers.remove("Location");
        body = new byte[0];
    }

    public void forward(final String path) throws IOException {
        try (final var resource = getClass().getClassLoader().getResourceAsStream("static" + path)) {
            if (resource == null) {
                sendError(404, "Not Found");
                return;
            }
            body(resource.readAllBytes(), getContentType(path));
        }
    }

    private String getContentType(final String path) {
        if (path.endsWith(".css")) {
            return "text/css;charset=utf-8";
        }
        if (path.endsWith(".js")) {
            return "application/javascript;charset=utf-8";
        }
        return "text/html;charset=utf-8";
    }

    public void flush() throws IOException {
        final var responseHeaders = new StringBuilder();
        responseHeaders.append("HTTP/1.1 ").append(status).append("\r\n");
        responseHeaders.append("Content-Type: ").append(contentType).append("\r\n");
        responseHeaders.append("Content-Length: ").append(body.length).append("\r\n");
        for (final var header : headers.entrySet()) {
            responseHeaders.append(header.getKey()).append(": ").append(header.getValue()).append("\r\n");
        }
        responseHeaders.append("\r\n");

        outputStream.write(responseHeaders.toString().getBytes(StandardCharsets.UTF_8));
        outputStream.write(body);
        outputStream.flush();
    }
}
