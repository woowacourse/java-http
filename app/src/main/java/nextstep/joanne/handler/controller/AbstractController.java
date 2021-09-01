package nextstep.joanne.handler.controller;

import nextstep.joanne.http.request.HttpRequest;
import nextstep.joanne.http.request.HttpRequest2;
import nextstep.joanne.http.response.HttpResponse;
import nextstep.joanne.http.response.HttpResponse2;

public class AbstractController implements Controller {
    @Override
    public void service(HttpRequest2 request, HttpResponse2 response) {

    }

    protected void doPost(HttpRequest request, HttpResponse response) {
    }

    protected void doGet(HttpRequest request, HttpResponse response) {
    }
}
