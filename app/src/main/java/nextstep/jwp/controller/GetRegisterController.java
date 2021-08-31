package nextstep.jwp.controller;

import nextstep.jwp.infrastructure.http.request.HttpRequest;
import nextstep.jwp.infrastructure.http.request.Method;
import nextstep.jwp.infrastructure.http.request.RequestLine;
import nextstep.jwp.infrastructure.http.view.ResourceView;
import nextstep.jwp.infrastructure.http.view.View;

public class GetRegisterController implements Controller {

    @Override
    public RequestLine requestLine() {
        return new RequestLine(Method.GET, "/register");
    }

    @Override
    public View handle(final HttpRequest request) {
        return new ResourceView("/register.html");
    }
}
