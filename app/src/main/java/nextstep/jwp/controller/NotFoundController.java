package nextstep.jwp.controller;

import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;

public class NotFoundController extends AbstractController {

    private static final String NOT_FOUND_PATH = "/404.html";

    @Override
    public HttpResponse process(HttpRequest request) {
        return HttpResponse.redirect(NOT_FOUND_PATH);
    }
}
