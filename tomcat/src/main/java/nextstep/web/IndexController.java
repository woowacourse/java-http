package nextstep.web;

import org.apache.coyote.http11.controller.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class IndexController extends AbstractController {

    @Override
    public void doGetRequest(final HttpRequest request, final HttpResponse response) {
        response.forwardTo("/index.html");
    }
}
