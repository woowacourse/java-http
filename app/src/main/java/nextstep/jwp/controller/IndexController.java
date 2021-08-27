package nextstep.jwp.controller;

import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;

public class IndexController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        response.forward("/index.html");
    }
}
