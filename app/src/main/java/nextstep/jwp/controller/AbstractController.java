package nextstep.jwp.controller;

import nextstep.jwp.exception.MethodNotAllowedException;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        if (request.getMethod().isGet()) {
            doGet(request, response);
            return;
        }
        if (request.getMethod().isPost()) {
            doPost(request, response);
            return;
        }
        throw new MethodNotAllowedException();
    }

    protected void doPost(HttpRequest request, HttpResponse response)
            throws Exception {
        throw new MethodNotAllowedException();
    }

    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        throw new MethodNotAllowedException();
    }
}