package nextstep.joanne.server.handler.controller;

import nextstep.joanne.server.handler.ErrorView;
import nextstep.joanne.server.http.HttpStatus;
import nextstep.joanne.server.http.request.ContentType;
import nextstep.joanne.server.http.request.HttpRequest;
import nextstep.joanne.server.http.response.HttpResponse;

public class ControllerAdvice {
    public static void handle(HttpRequest httpRequest, HttpResponse httpResponse, HttpStatus httpStatus) {
        mappedByStatusCode(httpRequest, httpResponse, httpStatus);
    }

    private static void mappedByStatusCode(HttpRequest httpRequest, HttpResponse httpResponse, HttpStatus httpStatus) {
        makeResponse(httpRequest, httpResponse, httpStatus);
    }

    private static void makeResponse(HttpRequest httpRequest, HttpResponse response, HttpStatus httpStatus) {
        response.addStatus(httpStatus);
        response.addHeaders("Content-Type", ContentType.resolve(httpRequest.uri()));
        response.addBody(ErrorView.viewOf(httpStatus));
    }
}
