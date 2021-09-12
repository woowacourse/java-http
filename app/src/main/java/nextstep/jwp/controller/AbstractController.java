package nextstep.jwp.controller;

import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        if (request.getMethod().isGet()) {
            doGet(request, response);
        }
        if (request.getMethod().isPost()) {
            doPost(request, response);
        }
    }

    protected void doPost(HttpRequest request, HttpResponse response)
            throws Exception {
    }

    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
    }
}