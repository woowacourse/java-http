package nextstep.jwp.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class FrontController extends AbstractController {

    @Override
    public void service(final HttpRequest request, final HttpResponse response) throws Exception {
        response.setRequest(request);
    }

}
