package nextstep.jwp.httpserver.handler.controller;

import nextstep.jwp.httpserver.domain.request.HttpRequest;
import nextstep.jwp.httpserver.domain.response.HttpResponse;

public interface Controller {
    void service(HttpRequest request, HttpResponse response) throws Exception;
}
