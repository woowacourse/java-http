package nextstep.jwp.httpserver.handler.controller;

import nextstep.jwp.httpserver.domain.request.HttpRequest;
import nextstep.jwp.httpserver.domain.response.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        if (request.isGet()) {
            doGet(request, response);
        }
        if (request.isPost()) {
            doPost(request, response);
        }
    }

    protected abstract void doGet(HttpRequest request, HttpResponse response) throws Exception;

    protected abstract void doPost(HttpRequest request, HttpResponse response) throws Exception;
}
