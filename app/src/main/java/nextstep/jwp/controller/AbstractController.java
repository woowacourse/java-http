package nextstep.jwp.controller;

import nextstep.jwp.HttpMethod;
import nextstep.jwp.http.HttpRequest;

public abstract class AbstractController implements Controller{

    @Override
    public boolean isMatchingController(HttpRequest httpRequest) {
        return httpRequest.getHttpMethod() == httpMethod() &&
            httpRequest.getPath().equals(uriPath());
    }

    abstract HttpMethod httpMethod();

    abstract String uriPath();
}
