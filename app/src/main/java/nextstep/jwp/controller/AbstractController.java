package nextstep.jwp.controller;

import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.util.Methods;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        if (Methods.isGet(request.method())) {
            doGet(request, response);
            return;
        }

        if (Methods.isPost(request.method())) {
            doPost(request, response);
        }
    }

    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {

    }

    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {

    }
}
