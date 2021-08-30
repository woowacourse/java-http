package nextstep.jwp.controller;

import nextstep.jwp.framework.http.HttpRequest;
import nextstep.jwp.framework.http.HttpResponse;

public class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        if (request.isPost()) {
            doPost(request, response);
            return;
        }
        doGet(request, response);
    }

    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {

    }

    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {

    }
}
