package nextstep.jwp.controller;

import nextstep.jwp.framework.http.common.HttpStatus;
import nextstep.jwp.framework.http.request.HttpRequest;
import nextstep.jwp.framework.http.response.HttpResponse;

public class DefaultController extends AbstractController {

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        response.create(request.getRequestLine(), request.getHeaders(), request.getBody(), HttpStatus.OK);
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        response.create(request.getRequestLine(), request.getHeaders(), request.getBody(), HttpStatus.OK);
    }
}
