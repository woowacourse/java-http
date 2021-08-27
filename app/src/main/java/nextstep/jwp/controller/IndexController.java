package nextstep.jwp.controller;

import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;

public class IndexController extends AbstractController {
    @Override
    public void doGet(HttpRequest request, HttpResponse response) {
        String url = "index.html";
        super.doGet(request, response);
    }
}
