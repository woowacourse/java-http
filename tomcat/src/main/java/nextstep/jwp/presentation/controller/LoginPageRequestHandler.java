package nextstep.jwp.presentation.controller;

import org.apache.coyote.http11.Request;
import org.apache.coyote.http11.Response;

public class LoginPageRequestHandler implements RequestHandler {
    @Override
    public String handle(final Request request, final Response response) {
        return "login";
    }

    @Override
    public boolean support(final Request request) {
        return request.getRequestURI().contains("login") && request.getQueryParams().isEmpty();
    }
}
