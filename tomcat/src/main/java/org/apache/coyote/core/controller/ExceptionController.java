package org.apache.coyote.core.controller;

import java.io.IOException;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.http.reqeust.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.io.ClassPathResource;

public class ExceptionController extends AbstractController {

    @Override
    public void service(final HttpRequest request, final HttpResponse response)
            throws IOException, UncheckedServletException {
        String responseBody = new ClassPathResource().getStaticContent(request.getPath());

        response.setStatus("OK");
        response.setContentType(request.findContentType());
        response.setContentLength(responseBody.getBytes().length);
        response.setResponseBody(responseBody);
    }
}
