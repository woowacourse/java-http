package nextstep.jwp.controller;

import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.http.HttpHeader;
import nextstep.jwp.http.HttpMethod;
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
