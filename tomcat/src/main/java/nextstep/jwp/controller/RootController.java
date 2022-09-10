package nextstep.jwp.controller;

import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.http.reqeust.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.http.response.StatusCode;

public class RootController extends AbstractController {

    private static final String ROOT_RESPONSE_BODY = "Hello world!";

    @Override
    public void service(final HttpRequest request, final HttpResponse response) throws UncheckedServletException {
        response.setStatusCode(StatusCode.OK);
        response.setContentLength(ROOT_RESPONSE_BODY.getBytes().length);
        response.setResponseBody(ROOT_RESPONSE_BODY);
    }
}
