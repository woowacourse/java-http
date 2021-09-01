package nextstep.joanne.server.handler.controller;

import nextstep.joanne.server.http.HttpStatus;
import nextstep.joanne.server.http.request.ContentType;
import nextstep.joanne.server.http.request.HttpRequest;
import nextstep.joanne.server.http.response.HttpResponse;

public class ControllerAdvice {
    public static void handle(HttpRequest httpRequest, HttpResponse httpResponse, HttpStatus httpStatus) {
        mappedByStatusCode(httpRequest, httpResponse, httpStatus);
    }

    private static void mappedByStatusCode(HttpRequest httpRequest, HttpResponse response, HttpStatus httpStatus) {
        if (httpStatus.equals(HttpStatus.UNAUTHORIZED)) {
            response.addStatus(HttpStatus.UNAUTHORIZED);
            response.addHeaders("Content-Type", ContentType.resolve(httpRequest.uri()));
            response.addBody(httpRequest.uri());
            return;
        }
        if (httpStatus.equals(HttpStatus.NOT_FOUND)) {
            response.addStatus(HttpStatus.NOT_FOUND);
            response.addHeaders("Content-Type", ContentType.resolve(httpRequest.uri()));
            response.addBody(httpRequest.uri());
            return;
        }
        response.addStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        response.addHeaders("Content-Type", ContentType.resolve(httpRequest.uri()));
        response.addBody(httpRequest.uri());
    }
}
