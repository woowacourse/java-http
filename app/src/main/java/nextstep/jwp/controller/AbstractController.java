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

    @Override
    public boolean matchRequest(HttpRequest request) {
        return request.matchURI(requestURI());
    }

    protected HttpResponse doGet(HttpRequest request) {
        return null;
    }

    protected HttpResponse doPost(HttpRequest request) {
        return null;
    }

    protected abstract String requestURI();
}
