package nextstep.jwp.web.controller;

import nextstep.jwp.web.network.request.HttpMethod;
import nextstep.jwp.web.network.request.HttpRequest;
import nextstep.jwp.web.network.response.HttpResponse;

public abstract class AbstractController implements Controller {

    private final String resource;

    protected AbstractController(String resource) {
        this.resource = resource;
    }

    @Override
    public String getResource() {
        return resource;
    }

    @Override
    public HttpResponse execute(HttpRequest httpRequest) {
        if (HttpMethod.POST.equals(httpRequest.getHttpMethod())) {
            return doPost(httpRequest);
        }
        return doGet(httpRequest);
    }

    protected HttpResponse doGet(HttpRequest httpRequest) {
        throw new UnsupportedOperationException();
    }

    protected HttpResponse doPost(HttpRequest httpRequest) {
        throw new UnsupportedOperationException();
    }
}
