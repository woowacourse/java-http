package nextstep.jwp.controller;

import nextstep.jwp.http.HttpContentType;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.http.HttpStatus;

public class ResourceController extends AbstractController {
    private final HttpContentType httpContentType;

    public ResourceController(final HttpContentType httpContentType) {
        this.httpContentType = httpContentType;
    }

    @Override
    void doGet(final HttpRequest request, HttpResponse response) {
        response.setResponse(getHttpContent(request).getResponse());
    }

    @Override
    void doPost(final HttpRequest request, HttpResponse response) {

    }

    private HttpResponse getHttpContent(final HttpRequest request) {
        return new HttpResponse(HttpStatus.OK, httpContentType, request.getUrl());
    }
}
