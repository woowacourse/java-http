package nextstep.jwp.presentation.controller;

import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.http11.http.HttpResponse;
import org.apache.coyote.http11.util.HttpMethod;
import org.apache.coyote.http11.http.RequestLine;
import org.apache.coyote.http11.util.HttpStatus;

public class RegisterPageRequestHandler implements RequestHandler {
    @Override
    public String handle(final HttpRequest request, final HttpResponse response) {
        response.setStatusCode(HttpStatus.OK);
        return "register";
    }

    @Override
    public boolean support(final HttpRequest request) {
        final RequestLine requestLine = request.getRequestLine();
        return requestLine.getRequestURI().contains("register") && (requestLine.getHttpMethod() == HttpMethod.GET);
    }
}
