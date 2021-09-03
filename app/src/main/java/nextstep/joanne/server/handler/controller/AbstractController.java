package nextstep.joanne.server.handler.controller;

import nextstep.joanne.server.http.HttpMethod;
import nextstep.joanne.server.http.request.HttpRequest;
import nextstep.joanne.server.http.response.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        if (request.isSameMethod(HttpMethod.GET)) {
            doGet(request, response);
        }
        if (request.isSameMethod(HttpMethod.POST)) {
            doPost(request, response);
        }
    }

    protected void doPost(HttpRequest request, HttpResponse response) {
    }

    protected void doGet(HttpRequest request, HttpResponse response) {
    }
}

