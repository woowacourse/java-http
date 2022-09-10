package nextstep.jwp.controller;

import nextstep.jwp.http.reqeust.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;

public class ExceptionHandler extends AbstractController {

    private static final String UNAUTHORIZED_ERROR_PAGE_URL = "./401.html";

    @Override
    public void service(final HttpRequest request, final HttpResponse response) {
        response.sendRedirect(UNAUTHORIZED_ERROR_PAGE_URL);
    }
}
