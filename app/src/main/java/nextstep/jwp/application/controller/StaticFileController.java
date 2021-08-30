package nextstep.jwp.application.controller;

import nextstep.jwp.webserver.AbstractController;
import nextstep.jwp.webserver.HttpRequest;
import nextstep.jwp.webserver.HttpResponse;
import nextstep.jwp.webserver.StatusCode;

public class StaticFileController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        String[] paths = request.getUri().split("/");
        String fileName = paths[paths.length - 1];
        String body = readStaticFile(fileName);
        response.setStatusCode(StatusCode._200_OK);
        response.setBody(body);
    }
}
