package nextstep.jwp.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;

public class IndexController extends AbstractController {

    @Override
    protected final HttpResponse doPost(HttpRequest request) {
        return doAction();
    }

    @Override
    protected final HttpResponse doGet(HttpRequest request) {
        return doAction();
    }

    private HttpResponse doAction() {
        return new HttpResponse()
                .status(HttpStatus.OK)
                .body("Hello world!");
    }
}
