package org.apache.coyote.http11;

import static org.apache.coyote.http11.ContentType.TEXT_HTML;
import static org.apache.coyote.http11.HttpStatus.INTERNAL_SERVER_ERROR;

import nextstep.jwp.common.ResourceLoader;

public class HttpExceptionHandler {

    private static final String RESOURCE_PATH_FORMAT = "static/%s.html";

    public void handleException(HttpException e, HttpResponse response) {
        HttpStatus httpStatus = e.httpStatus();
        int code = httpStatus.statusCode();
        String body = ResourceLoader.load(String.format(RESOURCE_PATH_FORMAT, code));
        response.setStatus(httpStatus);
        response.setHeader("Content-Type", TEXT_HTML.value());
        response.setBody(body);
    }

    public void setInternalServerError(HttpResponse response) {
        int code = INTERNAL_SERVER_ERROR.statusCode();
        String body = ResourceLoader.load(String.format(RESOURCE_PATH_FORMAT, code));
        response.setStatus(INTERNAL_SERVER_ERROR);
        response.setHeader("Content-Type", TEXT_HTML.value());
        response.setBody(body);
    }
}
