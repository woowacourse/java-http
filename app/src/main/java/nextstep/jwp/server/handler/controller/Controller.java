package nextstep.jwp.server.handler.controller;

import nextstep.jwp.http.header.request.HttpRequest;
import nextstep.jwp.http.header.response.Response;

public interface Controller {
    Response doService(HttpRequest httpRequest);
    boolean isSatisfiedBy(HttpRequest httpRequest);
}
