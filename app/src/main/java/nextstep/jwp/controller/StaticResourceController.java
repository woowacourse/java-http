package nextstep.jwp.controller;

import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.request.requestline.RequestLine;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.http.response.HttpStatus;

public class StaticResourceController implements Controller {

    private static final String NOT_FOUND_PATH = "/404.html";

    public StaticResourceController() {
    }

    @Override
    public HttpResponse process(HttpRequest request) {
        if (!request.isGet()) {
            return HttpResponse.redirect(NOT_FOUND_PATH);
        }
        final RequestLine requestLine = request.getRequestLine();
        return HttpResponse.of(HttpStatus.OK, requestLine.getRequestURI());
    }
}
