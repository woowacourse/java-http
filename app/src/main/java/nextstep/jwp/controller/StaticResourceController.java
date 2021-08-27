package nextstep.jwp.controller;

import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;

import static nextstep.jwp.http.request.HttpMethod.GET;

public class StaticResourceController extends AbstractController {

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        if (request.hasMethod(GET)) {
            doGet(request, response);
        }
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        assignStaticResourceByUriToResponse(request, response, "");
    }
}
