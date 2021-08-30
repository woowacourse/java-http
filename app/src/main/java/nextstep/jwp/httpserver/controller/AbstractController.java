package nextstep.jwp.httpserver.controller;

import java.util.Map;

import nextstep.jwp.httpserver.domain.HttpMethod;
import nextstep.jwp.httpserver.domain.request.HttpRequest;
import nextstep.jwp.httpserver.domain.response.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public HttpResponse service(HttpRequest httpRequest, Map<String, String> param) {
        final HttpMethod httpMethod = httpRequest.getHttpMethod();
        if (HttpMethod.isGet(httpMethod)) {
            return doGet(httpRequest, param);
        }
        return doPost(httpRequest, param);
    }

    protected abstract HttpResponse doGet(HttpRequest httpRequest, Map<String, String> param);

    protected abstract HttpResponse doPost(HttpRequest httpRequest, Map<String, String> param);
}
