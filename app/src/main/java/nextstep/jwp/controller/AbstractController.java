package nextstep.jwp.controller;

import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        if ("POST".equals(request.getMethod())) {
            doPost(request, response);
        }
        if ("GET".equals(request.getMethod())) {
            doGet(request, response);
        }
    }

    protected void doPost(HttpRequest request, HttpResponse response)
            throws Exception {
        ExceptionHandler.methodNotAllowed(response);
    }

    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        ExceptionHandler.methodNotAllowed(response);
    }
}