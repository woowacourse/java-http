package nextstep.jwp.presentation.controller;

import customservlet.RequestHandler;
import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.http11.http.HttpResponse;
import org.apache.coyote.http11.util.HttpStatus;

public class RegisterPageRequestHandler implements RequestHandler {

    @Override
    public String handle(final HttpRequest request, final HttpResponse response) {
        response.setStatusCode(HttpStatus.OK);
        return "register";
    }
}
