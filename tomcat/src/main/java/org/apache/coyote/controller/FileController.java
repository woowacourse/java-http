package org.apache.coyote.controller;

import org.apache.coyote.Controller;
import org.apache.coyote.controller.util.Extension;
import org.apache.coyote.controller.util.FileResolver;
import org.apache.coyote.controller.util.HttpResponse;
import org.apache.coyote.http11.http.HttpRequest;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class FileController extends Controller {

    private static final String STATIC = "static";

    private FileController() {
    }

    public static FileController getController() {
        return new FileController();
    }

    @Override
    public String run(final HttpRequest request) throws IOException {
        final String uri = request.getUri();
        Extension.validateContaining(uri);
        return createResponse(uri);
    }

    public String createResponse(final String uri) throws IOException {
        final String responseBody = getResponseBody(uri);
        return createResponseHeader(uri, responseBody);
    }

    private String getResponseBody(final String uri) throws IOException {
        if (uri.equals("/")) {
            return "Hello world!";
        }
        return new String(Files.readAllBytes(findFilePath(uri)));
    }

    private Path findFilePath(final String uri) {
        try {
            final URL url = ClassLoader.getSystemClassLoader().getResource(STATIC + uri);
            return new File(Objects.requireNonNull(url).getPath()).toPath();
        } catch (final NullPointerException exception) {
            throw new IllegalArgumentException("잘못된 파일 경로입니다.");
        }
    }

    private String createResponseHeader(final String uri, final String responseBody) {
        return joinResponses(
                HttpResponse.HTTP_200_OK.getValue(),
                Extension.getMappedContentType(uri).getContentType(),
                HttpResponse.getContentLength(responseBody),
                HttpResponse.EMPTY.getValue(),
                responseBody
        );
    }

    public String createResponse(final FileResolver file) throws IOException {
        final String responseBody = getResponseBody("/" + file.getFileName());
        return createResponseHeader(file.getFileName(), responseBody);
    }
}
