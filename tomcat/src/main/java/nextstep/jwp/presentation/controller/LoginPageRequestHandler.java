package nextstep.jwp.presentation.controller;

import java.util.Optional;
import org.apache.coyote.http11.http.SessionManager;
import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.http11.http.HttpResponse;
import org.apache.coyote.http11.http.RequestLine;
import org.apache.coyote.http11.util.HttpMethod;
import org.apache.coyote.http11.util.HttpStatus;

public class LoginPageRequestHandler implements RequestHandler {
    private final SessionManager sessionManager;

    public LoginPageRequestHandler() {
        this.sessionManager = new SessionManager();
    }

    @Override
    public String handle(final HttpRequest request, final HttpResponse response) {
        final Optional<String> jSessionId = request.getJSessionId();
        if (jSessionId.isPresent()) {
            System.out.println(jSessionId.get());
            if (sessionManager.hasSameSessionId(jSessionId.get())) {
                response.setStatusCode(HttpStatus.FOUND);
                return "index";
            }
        }
        response.setStatusCode(HttpStatus.OK);
        return "login";
    }

    @Override
    public boolean support(final HttpRequest request) {
        final RequestLine requestLine = request.getRequestLine();
        return requestLine.getRequestURI().contains("login") && (requestLine.getHttpMethod() == HttpMethod.GET);
    }
}
