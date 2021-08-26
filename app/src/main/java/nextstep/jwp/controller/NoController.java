package nextstep.jwp.controller;

import nextstep.jwp.HttpRequest;
import nextstep.jwp.HttpResponse;

public class NoController implements Controller {

    @Override
    public HttpResponse get(HttpRequest request) {
        return null;
    }
}
