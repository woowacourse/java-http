package nextstep.jwp.application.controller;

import nextstep.jwp.webserver.Controller;
import nextstep.jwp.webserver.HttpRequest;
import nextstep.jwp.webserver.HttpResponse;

public class HelloWorldController implements Controller {
    @Override
    public HttpResponse handle(HttpRequest request) {
        return HttpResponse.ok("Hello world!");
    }
}
