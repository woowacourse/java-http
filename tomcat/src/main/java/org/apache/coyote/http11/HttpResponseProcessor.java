package org.apache.coyote.http11;

import org.apache.coyote.HttpStatus;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class HttpResponseProcessor {
    private static final char CR = '\r';
    private static final char LF = '\n';
    private static final String STATIC_RESOURCE_PREFIX = "static";
    private static final byte[] DEFAULT_BODY = "Hello world!".getBytes();
    private static final String MIME_HTML = "text/html;charset=utf-8";
    private final OutputStream outputStream;


    public HttpResponseProcessor(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void send(String uri) throws IOException, URISyntaxException {
        URL resourceUri = getClass().getClassLoader().getResource(STATIC_RESOURCE_PREFIX + uri);
        if (resourceUri == null) {
            sendError(HttpStatus.NOT_FOUND);
            return;
        }

        Path resourcePath = Path.of(resourceUri.toURI());
        if ("/".equals(uri)) {
            send(HttpStatus.OK, MIME_HTML, DEFAULT_BODY);
            return;
        }
        if (resourcePath.toFile().exists()) {
            byte[] content = Files.readAllBytes(resourcePath);
            send(HttpStatus.OK, MIME_HTML, content);
        }
    }

    public void sendError(HttpStatus status) throws IOException {
        if (!status.isError()) {
            throw new IllegalArgumentException("에러 상태여야 합니다.");
        }
        send(status, "text/html;charset=utf-8", new byte[0]);
    }

    private void send(HttpStatus status, String contentType, byte[] body) throws IOException {
        final String head = String.join("\r\n",
                "HTTP/1.1 " + status.getCode() + " " + status.getMessage() + " ",
                "Content-Type: " + contentType + " ",
                "Content-Length: " + body.length + " ",
                "",
                "");

        outputStream.write(head.getBytes(StandardCharsets.ISO_8859_1));
        outputStream.write(body);
        outputStream.flush();
    }
}
