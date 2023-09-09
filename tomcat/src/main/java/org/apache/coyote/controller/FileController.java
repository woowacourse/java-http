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
import java.util.List;
import java.util.Objects;

public class FileController extends Controller {

    private static final List<String> resources = List.of(
            "chart-area.js",
            "chart-bar.js",
            "chart-pie.js",
            "script.js",
            "styles.css",
            "401.html",
            "401.html",
            "500.html",
            "index.html",
            "login.html",
            "register.html",
            "/"
    );
    private static final String STATIC = "static";

    private FileController() {
    }

    public static FileController from() {
        return new FileController();
    }

    @Override
    public String run(final HttpRequest request) throws IOException {
        validateExtension(request.getUri());
        return createResponse(request.getUri());
    }

    private void validateExtension(final String fileName) {
        final boolean containsExtension = resources.stream()
                                                   .anyMatch(fileName::contains);
        if (!containsExtension) {
            throw new IllegalArgumentException("확장자를 포함하고 있지 않습니다.");
        }
    }

    public String createResponse(final String uri) throws IOException {
        final String responseBody = getResponseBody(uri);
        return createResponseHeader(uri, responseBody);
    }

    public String createResponse(final FileResolver file) throws IOException {
        final String responseBody = getResponseBody("/" + file.getFileName());
        return createResponseHeader(file.getFileName(), responseBody);
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
}
