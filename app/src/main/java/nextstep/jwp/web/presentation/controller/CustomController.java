package nextstep.jwp.web.presentation.controller;

import nextstep.jwp.http.message.request.HttpRequest;
import nextstep.jwp.http.message.request.request_line.HttpMethod;
import nextstep.jwp.server.handler.controller.Controller;

public abstract class CustomController implements Controller {

    @Override
    public boolean isSatisfiedBy(HttpRequest httpRequest) {
        return httpRequest.getPath().getUri().equals(path()) &&
            httpRequest.getHttpMethod().equals(httpMethod());
    }

    protected abstract HttpMethod httpMethod();
    protected abstract String path();
}
