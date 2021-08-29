package nextstep.jwp.controller;

import nextstep.jwp.web.HttpRequest;
import nextstep.jwp.web.HttpResponse;
import nextstep.jwp.web.HttpStatus;

import java.nio.file.Files;
import java.nio.file.Path;

public class IndexController implements Controller {
    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        String indexFilePath = getClass().getClassLoader().getResource("static/index.html").getPath();

        response.status(HttpStatus.OK)
                .contentType("text/html;charset=utf-8")
                .body(new String(Files.readAllBytes(Path.of(indexFilePath))));
    }
}
