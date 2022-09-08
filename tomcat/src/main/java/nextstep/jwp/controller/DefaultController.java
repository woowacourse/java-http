package nextstep.jwp.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class DefaultController extends AbstractController {

    public DefaultController() {
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) {
        response.ok(request);
    }
}
