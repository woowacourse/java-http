package nextstep.jwp.application.controller;

import nextstep.jwp.webserver.AbstractController;
import nextstep.jwp.webserver.HttpRequest;
import nextstep.jwp.webserver.HttpResponse;
import nextstep.jwp.webserver.StatusCode;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class StaticFileController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        String body = respondStaticFile(request.getUri());
        response.setStatusCode(StatusCode._200_OK);
        response.setBody(body);
    }

    private String respondStaticFile(String uriPath) {
        String[] paths = uriPath.split("/");
        String fileName = paths[paths.length - 1];
        URL resource = getClass().getClassLoader().getResource("static/" + fileName);

        if (resource == null) {
            throw new RuntimeException(); // 파일없음
        }
        Path path = new File(resource.getPath()).toPath();

        try {
            return Files.readString(path);
        } catch (IOException e) {
            return "파일 read 중 에러 발생";
        }
    }
}
