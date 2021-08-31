package nextstep.jwp.controller;

import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.util.Controllers;

public class RequestMapping {

    public Controller getController(HttpRequest request) {
        return Controllers.matchController(request);
    }
}
