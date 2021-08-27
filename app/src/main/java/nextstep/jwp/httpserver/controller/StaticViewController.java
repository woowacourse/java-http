package nextstep.jwp.httpserver.controller;

import nextstep.jwp.httpserver.Handler;

public class StaticViewController implements Handler {

    @Override
    public String handle() {
        return "/";
    }
}
