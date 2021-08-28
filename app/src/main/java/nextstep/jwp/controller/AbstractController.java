package nextstep.jwp.controller;

import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public void process(HttpRequest request, HttpResponse response) {
        if (request.isGet()) {
            doGet(request, response);
        }
        if (request.isPost()) {
            doPost(request, response);
        }
    }

    protected void doGet(HttpRequest request, HttpResponse response) {
    }

    protected void doPost(HttpRequest request, HttpResponse response) {
    }
}
