package nextstep.jwp.application.controller;

import nextstep.jwp.webserver.Controller;
import nextstep.jwp.webserver.HttpRequest;

public class HelloWorldController implements Controller {
    @Override
    public String handle(HttpRequest request) {
        return "Hello world!";
    }
}
