package nextstep.jwp.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class IndexController extends AbstractController {

    @Override
    protected final HttpResponse doPost(final HttpRequest request) {
        return doAction();
    }

    @Override
    protected final HttpResponse doGet(final HttpRequest request) {
        return doAction();
    }

    private HttpResponse doAction() {
        return HttpResponse.ok()
                .body("Hello world!");
    }
}
