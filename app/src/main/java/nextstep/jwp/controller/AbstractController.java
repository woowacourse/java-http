package nextstep.jwp.controller;

import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public HttpResponse process(HttpRequest request) {
        if (request.isGet()) {
            return doGet(request);
        }
        return doPost(request);
    }

    protected HttpResponse doGet(HttpRequest request) {
        throw new UnsupportedOperationException();
    }

    protected HttpResponse doPost(HttpRequest request) {
        throw new UnsupportedOperationException();
    }
}
