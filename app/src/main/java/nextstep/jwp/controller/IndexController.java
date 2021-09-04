package nextstep.jwp.controller;

import nextstep.jwp.request.HttpRequest;
import nextstep.jwp.web.model.HttpSession;

public class IndexController implements Controller {

    public String index(HttpRequest httpRequest, HttpSession httpSession) {
        return "index.html";
    }
}
