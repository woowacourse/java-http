package nextstep.jwp.presentation.controller;

import static nextstep.jwp.presentation.ResourceLocation.ROOT;

import customservlet.RequestHandler;
import customservlet.SessionManager;
import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.http11.http.HttpResponse;
import org.apache.coyote.http11.http.Session;
import org.apache.coyote.http11.util.HttpStatus;

public class LoginPageRequestHandler implements RequestHandler {

    private final SessionManager sessionManager;

    public LoginPageRequestHandler() {
        this.sessionManager = SessionManager.getInstance();
    }

    @Override
    public String handle(final HttpRequest request, final HttpResponse response) {
        final Session session = request.getSession(true);
        if (sessionManager.isValid(session)) {
            response.setStatusCode(HttpStatus.FOUND);
            response.setLocation(ROOT.getLocation());
            return null;
        }
        response.setStatusCode(HttpStatus.OK);
        return "login";
    }
}
