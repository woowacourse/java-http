package nextstep.jwp.controller;

import nextstep.jwp.web.ContentType;
import nextstep.jwp.web.HttpRequest;
import nextstep.jwp.web.HttpResponse;
import nextstep.jwp.web.HttpStatus;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class StaticResourceController extends AbstractController {
    @Override
    public void doGet(HttpRequest request, HttpResponse response) throws Exception {
        String requestedResourcePath = request.getRequestUri().toString();
        URL resource = getClass().getClassLoader().getResource("static" + requestedResourcePath);
        String resourcePath = resource.getPath();

        if (resourcePath == null) {
            responseNotFound(response);
            return;
        }

        response.status(HttpStatus.OK)
                .contentType(ContentType.toHttpNotationFromFileExtension(resource.getFile()))
                .body(new String(Files.readAllBytes(Path.of(resourcePath))));
    }

    private void responseNotFound(HttpResponse response) throws IOException {
        // todo NotFoundController와의 중복 코드 줄이기
        URL resource = getClass().getClassLoader().getResource("static/404.html");
        String notFoundHtmlPagePath = resource.getPath();

        response.status(HttpStatus.NOT_FOUND)
                .contentType(ContentType.toHttpNotationFromFileExtension(resource.getFile()))
                .body(new String(Files.readAllBytes(Path.of(notFoundHtmlPagePath))));

    }
}
