package nextstep.jwp.controller;

import nextstep.jwp.model.httpMessage.HttpMethod;
import nextstep.jwp.model.httpMessage.request.HttpRequest;
import nextstep.jwp.model.httpMessage.HttpResponse;

import java.io.IOException;

public abstract class AbstractController implements Controller {
    @Override
    public void service(HttpRequest request, HttpResponse response) throws IOException {
        HttpMethod method = request.getMethod();

        if (method.isPost()) {
            doPost(request, response);
            return;
        }

        doGet(request, response);
    }

    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
    }

    protected void doPost(HttpRequest request, HttpResponse response) throws IOException {
    }
}
