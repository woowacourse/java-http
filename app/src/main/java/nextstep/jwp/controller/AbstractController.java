package nextstep.jwp.controller;

import java.io.IOException;

import nextstep.jwp.request.HttpRequest;
import nextstep.jwp.response.HttpResponse;

public abstract class AbstractController implements Controller {
    @Override
    public void process(HttpRequest request, HttpResponse response) throws IOException {
        if (request.isGet()) {
            doGet(request, response);
        } else if (request.isPost()) {
            doPost(request, response);
        }
    }

    protected void doGet(HttpRequest request, HttpResponse response) throws IOException { /* NOOP */ }

    protected void doPost(HttpRequest request, HttpResponse response) throws IOException { /* NOOP */ }
}
