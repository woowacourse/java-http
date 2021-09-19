package nextstep.jwp.controller;

import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.http.response.HttpStatus;
import nextstep.jwp.utils.ContentType;
import nextstep.jwp.utils.FileReader;
import nextstep.jwp.utils.Resources;

public class ExceptionHandler {

    private ExceptionHandler() {
    }

    public static void unauthorized(HttpResponse response) throws Exception {
        String content = FileReader.file(Resources.UNAUTHORIZED.getResource());
        addHeaders(response, content);
        response.setHttpStatus(HttpStatus.UNAUTHORIZED);
        response.setBody(content);
    }

    public static void notFound(HttpResponse response) throws Exception {
        String content = FileReader.file(Resources.NOT_FOUND.getResource());
        addHeaders(response, content);
        response.setHttpStatus(HttpStatus.NOT_FOUND);
        response.setBody(content);
    }

    public static void methodNotAllowed(HttpResponse response) throws Exception {
        String content = FileReader.file(Resources.METHOD_NOT_ALLOWED.getResource());
        addHeaders(response, content);
        response.setHttpStatus(HttpStatus.METHOD_NOT_ALLOWED);
        response.setBody(content);
    }

    public static void internalServerError(HttpResponse response) throws Exception {
        String content = FileReader.file(Resources.INTERNAL_SERVER_ERROR.getResource());
        addHeaders(response, content);
        response.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        response.setBody(content);
    }

    private static void addHeaders(HttpResponse response, String content) {
        response.addHeaders("Content-Type", ContentType.HTML.getType());
        response.addHeaders("Content-Length", String.valueOf(content.getBytes().length));
    }
}
