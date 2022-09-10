package nextstep.jwp.controller;

import jakarta.http.reqeust.HttpRequest;
import jakarta.http.response.HttpResponse;
import jakarta.http.response.StatusCode;

public class RootController extends AbstractController {

    private static final String ROOT_RESPONSE_BODY = "Hello world!";

    @Override
    public void service(final HttpRequest request, final HttpResponse response) {
        response.setStatusCode(StatusCode.OK);
        response.setContentLength(ROOT_RESPONSE_BODY.getBytes().length);
        response.setResponseBody(ROOT_RESPONSE_BODY);
    }
}
