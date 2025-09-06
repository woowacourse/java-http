package org.apache.coyote.http11;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class HttpResponse {

    private final OutputStream outputStream;

    public HttpResponse(OutputStream outputStream) {
        this.outputStream = outputStream;
    }


    public void sendOk(String contentType, byte[] body) throws IOException {
        sendResponse("200 OK", contentType, body);
    }

    private void sendResponse(String statusLine, String contentType, byte[] body) throws IOException {
        final var response = String.join("\r\n",
                "HTTP/1.1 " + statusLine + " ",
                "Content-Type: " + contentType + " ",
                "Content-Length: " + body.length + " ",
                "",
                "");

        outputStream.write(response.getBytes(StandardCharsets.UTF_8));
        outputStream.write(body);
        outputStream.flush();
    }

    public void sendNotFound() throws IOException {
        final var responseBody = "404 Not Found";
        final var bodyBytes = responseBody.getBytes(StandardCharsets.UTF_8);
        sendResponse("404 Not Found", "text/plain;charset=utf-8", bodyBytes);
    }

    public void sendBadRequest() throws IOException {
        final var responseBody = "400 Bad Request";
        final var bodyBytes = responseBody.getBytes(StandardCharsets.UTF_8);
        sendResponse("400 Bad Request", "text/html;charset=utf-8", bodyBytes);
    }

    public void sendInternalServerError() throws IOException {
        final var responseBody = "500 Internal Server Error";
        final var bodyBytes = responseBody.getBytes(StandardCharsets.UTF_8);
        sendResponse("500 Internal Server Error", "text/html;charset=utf-8", bodyBytes);
    }
}
