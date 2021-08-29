package nextstep.jwp.application.controller;

import static nextstep.jwp.web.http.request.HttpMethod.GET;

import nextstep.jwp.web.http.request.HttpRequest;
import nextstep.jwp.web.http.request.MethodUrl;
import nextstep.jwp.web.http.request.QueryParams;
import nextstep.jwp.web.http.response.HttpResponse;
import nextstep.jwp.web.mvc.controller.AbstractController;

public class WelcomeController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        request.changeMethodUrl(new MethodUrl(GET, "/index", QueryParams.empty()));
        super.doGet(request, response);
    }
}
