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

//        String accept = request.getHeader("Accept");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", getContentType(extension));
        response.setHeaders(headers);
    }

    private String getContentType(String extension) {
        if ("js".equals(extension)) {
            return "text/javascript";
        }
        if ("css".equals(extension)) {
            return "text/css";
        }
        if ("html".equals(extension)) {
            return "text/html";
        }
        return "text/plain";
    }
}
