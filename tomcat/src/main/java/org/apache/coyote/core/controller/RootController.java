package org.apache.coyote.core.controller;

import java.io.IOException;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.http.reqeust.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;

public class RootController extends AbstractController {

    private static final String ROOT_RESPONSE_BODY = "Hello world!";

    @Override
    public void service(final HttpRequest request, final HttpResponse response)
            throws IOException, UncheckedServletException {
        response.setStatus("OK");
        response.setContentType(request.findContentType());
        response.setContentLength(ROOT_RESPONSE_BODY.getBytes().length);
        response.setResponseBody(ROOT_RESPONSE_BODY);
    }
}
