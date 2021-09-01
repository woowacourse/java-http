package nextstep.jwp.framework.controller;

import nextstep.jwp.framework.infrastructure.http.method.HttpMethod;
import nextstep.jwp.framework.infrastructure.http.request.HttpRequest;
import nextstep.jwp.framework.infrastructure.http.response.HttpResponse;

public abstract class AbstractController implements Controller {

    public HttpResponse doService(HttpRequest httpRequest) {
        HttpMethod httpMethod = httpRequest.getMethod();
        if (httpMethod.equals(HttpMethod.GET)) {
            return doGet(httpRequest);
        }
        return doPost(httpRequest);
    }

    protected abstract HttpResponse doGet(HttpRequest httpRequest);

    protected abstract HttpResponse doPost(HttpRequest httpRequest);
}
