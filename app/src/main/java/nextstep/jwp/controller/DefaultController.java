package nextstep.jwp.controller;

import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.http.HttpStatus;

import java.io.IOException;

public class DefaultController extends AbstractController {
    @Override
    protected void doGet(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        httpResponse.setStatus(HttpStatus.OK_200);
        httpResponse.setBody("Hello world!");
        httpResponse.setPath(httpRequest.getPath());
        httpResponse.forward();
    }

    @Override
    protected void doPost(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {

    }
}
