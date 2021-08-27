package nextstep.jwp.dashboard;

import nextstep.jwp.httpserver.Handler;

public class HomeController implements Handler {

    @Override
    public String handle() {
        return "index";
    }
}
