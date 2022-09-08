package nextstep.jwp.presentation.controller;

import java.util.Optional;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.Http11Request;
import org.apache.coyote.http11.Http11Response;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.RequestLine;
import org.apache.coyote.http11.util.HttpStatus;

public class LoginPageRequestHandler implements RequestHandler {
    private final SessionManager sessionManager;

    public LoginPageRequestHandler() {
        this.sessionManager = new SessionManager();
    }

    @Override
    public String handle(final Http11Request request, final Http11Response response) {
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
    public boolean support(final Http11Request request) {
        final RequestLine requestLine = request.getRequestLine();
        return requestLine.getRequestURI().contains("login") && (requestLine.getHttpMethod() == HttpMethod.GET);
    }
}
