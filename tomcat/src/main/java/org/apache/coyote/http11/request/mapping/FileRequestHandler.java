package org.apache.coyote.http11.request.mapping;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class FileRequestHandler implements RequestHandler {

    private final String filePath;

    public FileRequestHandler(final String filePath) {
        this.filePath = filePath;
    }

    public static FileRequestHandler from(final String uriPath) {
        final String filePath = Objects.requireNonNull(FileRequestHandler.class
                .getClassLoader()
                .getResource("static" + uriPath)
        ).getPath();

        return new FileRequestHandler(filePath);
    }

    @Override
    public HttpResponse handle(final HttpRequest httpRequest) {
        try {
            final String responseBody = new String(Files.readAllBytes(Path.of(filePath)));
            return new HttpResponse(
                    ContentType.fromFilePath(httpRequest.getUriPath()),
                    HttpStatus.OK,
                    new HttpHeaders(Map.of()),
                    responseBody
            );
        } catch (final IOException e) {
            throw new NoSuchElementException();
        }
    }
}
