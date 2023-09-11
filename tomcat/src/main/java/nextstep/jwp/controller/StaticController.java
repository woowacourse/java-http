package nextstep.jwp.controller;

import org.apache.coyote.handler.AbstractController;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatusCode;
import org.apache.coyote.http11.ViewResolver;

public class StaticController extends AbstractController {

    @Override
    public void doGet(final HttpRequest request, final HttpResponse response) {
        final String responseBody = ViewResolver.read(request.getRequestUri());
        response.setResponseBody(responseBody);
        response.setContentLength();
        response.setStatusCode(HttpStatusCode.OK);
    }
}
