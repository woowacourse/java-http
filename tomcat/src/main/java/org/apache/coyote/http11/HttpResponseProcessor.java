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
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class HttpResponseProcessor {
    private static final char CR = '\r';
    private static final char LF = '\n';
    private static final String STATIC_RESOURCE_PREFIX = "static";
    private final OutputStream outputStream;
    private final Map<String, String> cookies;

    public HttpResponseProcessor(OutputStream outputStream) {
        this.outputStream = outputStream;
        this.cookies = new HashMap<>();
    }

    public void sendStaticResource(String path) throws IOException, URISyntaxException {
        Optional<Path> resource = findStaticResource(path);
        if (resource.isEmpty()) {
            sendError(HttpStatus.NOT_FOUND);
            return;
        }

        Path resourcePath = resource.get();
        String fileExtension = getFileExtensionOf(resourcePath);
        MimeType mimeType = MimeType.determineFromFileName(fileExtension);
        sendStaticResource(HttpStatus.OK, mimeType, Files.readAllBytes(resourcePath));
    }

    private Optional<Path> findStaticResource(String path) throws URISyntaxException {
        URL resourceUrl = getClass().getClassLoader().getResource(STATIC_RESOURCE_PREFIX + path);
        if (resourceUrl == null) {
            return Optional.empty();
        }
        return Optional.of(Path.of(resourceUrl.toURI()))
                .filter(Files::isRegularFile);
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
        sendStaticResource(status, MimeType.TEXT_HTML, new byte[0]);
    }

    public void sendStaticResource(HttpStatus status, MimeType mimeType, byte[] body) throws IOException {
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

    public void sendRedirect(String path) throws IOException {
        HttpStatus found = HttpStatus.FOUND;
        final String head = String.join("\r\n",
                "HTTP/1.1 " + found.getCode() + " " + found.getMessage() + " ",
                "Location: " + path + " ",
                "Content-Length: 0 ",
                "",
                "");

        outputStream.write(head.getBytes(StandardCharsets.ISO_8859_1));
        outputStream.flush();
    }
}
