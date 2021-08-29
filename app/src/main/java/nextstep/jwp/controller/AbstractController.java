package nextstep.jwp.controller;

import java.io.IOException;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;

public class AbstractController implements Controller {

    @Override
    public void process(HttpRequest request, HttpResponse response) throws IOException {
        if (request.isGet()) {
            doGet(request, response);
        }
        if (request.isPost()) {
            doPost(request, response);
        }
    }

    protected void doPost(HttpRequest request, HttpResponse response) {
    }

    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
    }
}
