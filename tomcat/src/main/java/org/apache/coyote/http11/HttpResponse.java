package org.apache.coyote.http11;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpResponse {

    private final OutputStream outputStream;
    private final Map<String, List<String>> headers = new HashMap<>();

    public HttpResponse(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void addHeader(String key, String value) {
        headers.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
    }

    public void setCookie(String name, String value) {
        addHeader("Set-Cookie", name + "=" + value);
    }

    private void sendResponse(String statusLine, String contentType, byte[] body) throws IOException {
        addHeader("Content-Type", contentType);
        addHeader("Content-Length", String.valueOf(body.length));
        StringBuilder formattedHeader = new StringBuilder();
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            for (String value : entry.getValue()) {
                formattedHeader.append(entry.getKey()).append(": ").append(value).append("\r\n");
            }
        }
        final var response = String.join("\r\n",
                "HTTP/1.1 " + statusLine,
                formattedHeader.toString(),
                "",
                "");
        outputStream.write(response.getBytes(StandardCharsets.UTF_8));
        outputStream.write(body);
        outputStream.flush();
    }

    public void sendRedirect(String location) throws IOException {
        addHeader("Location", location);
        sendResponse("302 Found", null, new byte[0]);
    }

    public void sendOk(String contentType, byte[] body) throws IOException {
        sendResponse("200 OK", contentType, body);
    }

    public void sendNotFound() throws IOException {
        final var bodyBytes = "404 Not Found".getBytes(StandardCharsets.UTF_8);
        sendResponse("404 Not Found", "text/plain;charset=utf-8", bodyBytes);
    }

    public void sendBadRequest() throws IOException {
        final var bodyBytes = "400 Bad Request".getBytes(StandardCharsets.UTF_8);
        sendResponse("400 Bad Request", "text/html;charset=utf-8", bodyBytes);
    }

    public void sendInternalServerError() throws IOException {
        final var bodyBytes = "500 Internal Server Error".getBytes(StandardCharsets.UTF_8);
        sendResponse("500 Internal Server Error", "text/html;charset=utf-8", bodyBytes);
    }
}
