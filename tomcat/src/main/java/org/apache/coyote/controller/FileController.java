package org.apache.coyote.controller;

import org.apache.coyote.Controller;
import org.apache.coyote.controller.util.Extension;
import org.apache.coyote.controller.util.HttpResponse;
import org.apache.coyote.http11.http.HttpRequest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

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
            "register.html"
    );
    private static final String STATIC = "static";

    private FileController() {
    }

    public static FileController from(final String fileName) {
        validateExtension(fileName);
        return new FileController();
    }

    private static void validateExtension(final String fileName) {
        resources.stream()
                 .filter(fileName::contains)
                 .findAny()
                 .orElseThrow(() -> new IllegalArgumentException("해당 파일이 존재하지 않습니다. " + fileName));
    }

    @Override
    public String run(final HttpRequest request) throws IOException {
        validateExtension(request.getUri());
        return getResponse(request.getUri());
    }

    @Override
    public String getResponse(final String uri) throws IOException {
        final var resource = ClassLoader.getSystemClassLoader().getResource(STATIC + uri);
        final String responseBody = new String(Files.readAllBytes(new File(resource.getPath()).toPath()));
        return String.join(
                "\r\n",
                HttpResponse.HTTP_200_OK.getValue(),
                Extension.getMappedContentType(uri).getValue(),
                HttpResponse.getContentLength(responseBody),
                HttpResponse.EMPTY.getValue(),
                responseBody
        );
    }
}
