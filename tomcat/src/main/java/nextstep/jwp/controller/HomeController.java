package nextstep.jwp.controller;

import org.apache.coyote.http11.controller.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class HomeController extends AbstractController {

    @Override
    protected HttpResponse doGet(final HttpRequest request) {
        return HttpResponse.ok()
                .textBody("Hello world!")
                .build();
    }
}
