package nextstep.jwp.application.controller;

import nextstep.jwp.webserver.AbstractController;
import nextstep.jwp.webserver.HttpRequest;
import nextstep.jwp.webserver.HttpResponse;
import nextstep.jwp.webserver.StatusCode;

public class StaticFileController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        String body = readStaticFile(request.getUri());
        response.addHeaders("Content-Type", getContentType(parseExtension(request)) + ";charset=utf-8");
        response.setStatusCode(StatusCode._200_OK);
        response.setBody(body);
    }

    private String parseExtension(HttpRequest request) {
        String[] paths = request.getUri().split("\\.");
        return paths[paths.length - 1];
    }

    @Override
    public String mappingUri() {
        throw new UnsupportedOperationException();
    }
}
