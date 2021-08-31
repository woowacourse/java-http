package nextstep.jwp.controller;

import nextstep.jwp.request.HttpRequest;

public class IndexController implements Controller {

    public String index(HttpRequest httpRequest) {
        return "index.html";
    }
}
