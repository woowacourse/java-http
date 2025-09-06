package org.apache.coyote.http11;

import static org.apache.coyote.http11.HttpStatus.NOT_FOUND;
import static org.apache.coyote.http11.HttpStatus.OK;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class HttpResponse {

    private final OutputStream outputStream;

    public HttpResponse(final OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void ok(final byte[] body) throws IOException {
        sendResponse(body, OK);
    }

    public void ok(final String filePath) throws IOException {
        try (InputStream fileInputStream = getClass().getClassLoader().getResourceAsStream(filePath)) {
            if (fileInputStream == null) {
                sendResponse(new byte[0], NOT_FOUND);
                return;
            }
            byte[] body = fileInputStream.readAllBytes();
            sendResponse(body, OK);
        }
    }

    private void sendResponse(byte[] body, HttpStatus httpStatus) throws IOException {
        final var response = String.join("\r\n",
                "HTTP/1.1 " + httpStatus.getStatusCode() + " " + httpStatus.getReasonPhrase() + " ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + body.length + " ",
                "",
                "");
        outputStream.write(response.getBytes(StandardCharsets.UTF_8));
        outputStream.write(body);
        outputStream.flush();
    }
}
