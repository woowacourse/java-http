package nextstep.jwp.server.handler.controller;

import nextstep.jwp.http.message.request.HttpRequest;
import nextstep.jwp.http.message.response.Response;

public interface Controller {
    Response doService(HttpRequest httpRequest);
    boolean isSatisfiedBy(HttpRequest httpRequest);
}
