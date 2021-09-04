package nextstep.jwp.controller;

import java.io.IOException;
import nextstep.jwp.framework.http.common.HttpStatus;
import nextstep.jwp.framework.http.request.HttpRequest;
import nextstep.jwp.framework.http.response.HttpResponse;

public class DefaultController extends AbstractController {

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws IOException {
        response.create(request.getRequestLine(), request.getHeaders(), HttpStatus.OK);
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        response.create(request.getRequestLine(), request.getHeaders(), HttpStatus.OK);
    }
}
