package nextstep.jwp.httpserver.handler.controller;

import nextstep.jwp.httpserver.domain.request.HttpRequest;
import nextstep.jwp.httpserver.domain.response.HttpResponse;

public class StaticResourceController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        response.ok();
    }
}
