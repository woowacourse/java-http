package nextstep.jwp.controller;

import nextstep.jwp.web.HttpRequest;
import nextstep.jwp.web.HttpResponse;
import nextstep.jwp.web.HttpStatus;

import java.nio.file.Files;
import java.nio.file.Path;

public class NotFoundController extends AbstractController {
    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        String notFoundHtmlPagePath = getClass().getClassLoader().getResource("static/404.html").getPath();

        response.status(HttpStatus.NOT_FOUND)
                .contentType("text/html;charset=utf-8")
                .body(new String(Files.readAllBytes(Path.of(notFoundHtmlPagePath))));
    }
}
