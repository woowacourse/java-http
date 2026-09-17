package org.apache.coyote.http11;

import org.apache.coyote.HttpStatus;
import org.apache.coyote.MimeType;

import javax.annotation.Nonnull;
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
            send(HttpStatus.OK, MimeType.TEXT_HTML, DEFAULT_BODY);
            return;
        }
        if (resourcePath.toFile().exists()) {
            byte[] content = Files.readAllBytes(resourcePath);
            String fileExtension = getFileExtensionOf(resourcePath);
            MimeType mimeType = MimeType.determineFromFileName(fileExtension);
            send(HttpStatus.OK, mimeType, content);
        }
    }

    @Nonnull
    private static String getFileExtensionOf(Path resourcePath) {
        if (resourcePath == null) {
            return "";
        }
        String fileName = resourcePath.getFileName().toString();
        int lastIndexOfDot = fileName.lastIndexOf('.');

        if (lastIndexOfDot == -1) {
            return "";
        }

        return fileName.substring(lastIndexOfDot + 1);
    }

    public void sendError(HttpStatus status) throws IOException {
        if (!status.isError()) {
            throw new IllegalArgumentException("Http 상태코드가 에러 상태여야 합니다.");
        }
        send(status, MimeType.TEXT_HTML, new byte[0]);
    }

    private void send(HttpStatus status, MimeType mimeType, byte[] body) throws IOException {
        final String head = String.join("\r\n",
                "HTTP/1.1 " + status.getCode() + " " + status.getMessage() + " ",
                "Content-Type: " + mimeType.getTypeName() + " ",
                "Content-Length: " + body.length + " ",
                "",
                "");

        outputStream.write(head.getBytes(StandardCharsets.ISO_8859_1));
        outputStream.write(body);
        outputStream.flush();
    }
}
