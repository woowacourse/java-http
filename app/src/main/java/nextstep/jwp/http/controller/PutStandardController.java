package nextstep.jwp.http.controller;

import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;

public class PutStandardController extends StandardController {

    @Override
    public HttpResponse doService(HttpRequest httpRequest) {
        return null;
    }

    @Override
    public boolean isSatisfiedBy(HttpRequest httpRequest) {
        return false;
    }
}
