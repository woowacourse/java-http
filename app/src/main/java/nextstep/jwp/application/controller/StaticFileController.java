package nextstep.jwp.application.controller;

import nextstep.jwp.webserver.*;

public class StaticFileController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        String[] paths = request.getUri().split("\\.");
        String extension = paths[paths.length - 1];

        String body = readStaticFile(request.getUri());
        response.setStatusCode(StatusCode._200_OK);
        response.setBody(body);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", getContentType(extension) + ";charset=utf-8");
        response.setHeaders(headers);
    }

    @Override
    public String mappingUri() {
        throw new UnsupportedOperationException();
    }
}
