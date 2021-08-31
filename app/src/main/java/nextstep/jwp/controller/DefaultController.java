package nextstep.jwp.controller;

import nextstep.jwp.framework.http.HttpRequest;
import nextstep.jwp.framework.http.HttpResponse;
import nextstep.jwp.framework.http.HttpStatus;

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
