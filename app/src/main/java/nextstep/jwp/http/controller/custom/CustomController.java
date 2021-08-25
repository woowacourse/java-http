package nextstep.jwp.http.controller.custom;

import nextstep.jwp.http.controller.Controller;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.request.request_line.HttpMethod;

public abstract class CustomController implements Controller {

    @Override
    public boolean isSatisfiedBy(HttpRequest httpRequest) {
        return httpRequest.getPath().getUri().equals(path()) &&
            httpRequest.getHttpMethod().equals(httpMethod());
    }

    protected abstract HttpMethod httpMethod();
    protected abstract String path();
}
