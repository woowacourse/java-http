package org.apache.coyote.http11;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class HttpResponse {

    private final OutputStream outputStream;

    public HttpResponse(OutputStream outputStream) {
        this.outputStream = outputStream;
    }


    public void ok(String contentType, byte[] body) throws IOException {
        sendResponse("200 OK", contentType, body);
    }

    public void notFound() throws IOException {
        final var responseBody = "404 Not Found";
        final var bodyBytes = responseBody.getBytes(StandardCharsets.UTF_8);
        sendResponse("404 Not Found", "text/plain;charset=utf-8", bodyBytes);
    }

    private void sendResponse(String statusLine, String contentType, byte[] body) throws IOException {
        final var response = String.join("\r\n",
                "HTTP/1.1 " + statusLine,
                "Content-Type: " + contentType,
                "Content-Length: " + body.length,
                "",
                "");

        outputStream.write(response.getBytes(StandardCharsets.UTF_8));
        outputStream.write(body);
        outputStream.flush();
    }
}
