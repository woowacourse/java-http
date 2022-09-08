package nextstep.jwp.controller;

import org.apache.coyote.web.request.HttpRequest;
import org.apache.coyote.web.response.HttpResponse;

public class DefaultController extends AbstractController {

    @Override
    protected void doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        httpResponse.forwardDefault();
    }
}
