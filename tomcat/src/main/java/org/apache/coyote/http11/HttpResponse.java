package org.apache.coyote.http11;

import static org.apache.coyote.http11.ContentType.CSS;
import static org.apache.coyote.http11.ContentType.HTML;
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
        sendResponse(body, HTML, OK);
    }

    public void ok(final String filePath) throws IOException {
        try (InputStream fileInputStream = getClass().getClassLoader().getResourceAsStream(filePath)) {
            if (fileInputStream == null) {
                sendResponse(new byte[0], HTML, NOT_FOUND);
                return;
            }
            byte[] body = fileInputStream.readAllBytes();
            sendResponse(body, getContentType(filePath), OK);
        }
    }

    private void sendResponse(byte[] body, ContentType contentType, HttpStatus httpStatus) throws IOException {
        final var response = String.join("\r\n",
                "HTTP/1.1 " + httpStatus.getStatusCode() + " " + httpStatus.getReasonPhrase() + " ",
                "Content-Type: " + contentType.getValue() + " ",
                "Content-Length: " + body.length + " ",
                "",
                "");
        outputStream.write(response.getBytes(StandardCharsets.UTF_8));
        outputStream.write(body);
        outputStream.flush();
    }

    private ContentType getContentType(String filePath) throws IOException {
        if (filePath.endsWith(".html")) {
            return HTML;
        }
        if (filePath.endsWith(".css")) {
            return CSS;
        }
        throw new IOException("지원하지 않는 파일 형식입니다.");
    }
}
