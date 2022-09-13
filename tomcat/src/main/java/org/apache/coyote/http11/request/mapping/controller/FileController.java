package org.apache.coyote.http11.request.mapping.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.header.HttpHeaders;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.mapping.MappingKey;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.support.FileUtils;

public class FileController implements Controller {

    private final String filePath;

    public FileController(final String filePath) {
        this.filePath = filePath;
    }

    public static FileController from(final MappingKey mappingKey) {
        final String filePath = FileUtils.getStaticFilePathFromUri(mappingKey.getUri());

        return new FileController(filePath);
    }

    @Override
    public HttpResponse handle(final HttpRequest httpRequest) {
        try {
            final String responseBody = new String(Files.readAllBytes(Path.of(filePath)));
            return new HttpResponse(
                    ContentType.fromFilePath(httpRequest.getUriPath()),
                    HttpStatus.OK,
                    new HttpHeaders(new ArrayList<>()),
                    responseBody
            );
        } catch (final IOException e) {
            throw new NoSuchElementException();
        }
    }
}
