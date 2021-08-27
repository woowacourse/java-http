package nextstep.jwp.controller;

import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;

public class NoController implements Controller {

    @Override
    public HttpResponse get(HttpRequest request) {
        return null;
    }

    @Override
    public HttpResponse post(HttpRequest request) {
        return null;
    }
}
