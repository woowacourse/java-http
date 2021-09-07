package nextstep.jwp.controller;

import nextstep.jwp.request.HttpRequest;
import nextstep.jwp.response.HttpResponse;

public class IndexController implements Controller {

    public String index(HttpRequest httpRequest, HttpResponse httpResponse) {
        return "index.html";
    }
}
