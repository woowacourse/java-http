package nextstep.jwp.controller;

import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.util.Controllers;

public class RequestMapping {

    public Controller getController(HttpRequest request) {
        return Controllers.matchController(request);
    }
}
