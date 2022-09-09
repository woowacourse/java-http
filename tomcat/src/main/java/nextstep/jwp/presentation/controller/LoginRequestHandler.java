package nextstep.jwp.presentation.controller;

import nextstep.jwp.SessionManager;
import nextstep.jwp.application.MemberService;
import nextstep.jwp.dto.request.LoginRequest;
import nextstep.jwp.presentation.resolver.FormDataResolver;
import org.apache.coyote.http11.http.Cookies;
import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.http11.http.HttpResponse;
import org.apache.coyote.http11.http.Location;
import org.apache.coyote.http11.http.RequestLine;
import org.apache.coyote.http11.util.HttpMethod;
import org.apache.coyote.http11.util.HttpStatus;

public class LoginRequestHandler implements RequestHandler {

    private final MemberService memberService;
    private final SessionManager sessionManager;

    public LoginRequestHandler() {
        this.memberService = new MemberService();
        this.sessionManager = new SessionManager();
    }

    @Override
    public String handle(final HttpRequest request, final HttpResponse response) {
        final var session = request.getSession(true);
        if (request.hasCookieByJSessionId()) {
            sessionManager.findSession(session.getId());
            response.setStatusCode(HttpStatus.FOUND);
            response.setLocation(Location.from("/index.html"));
            return null;
        }
        final String requestBody = request.getRequestBody();
        final LoginRequest loginRequest = LoginRequest.from(FormDataResolver.resolve(requestBody));
        memberService.login(loginRequest);
        response.setStatusCode(HttpStatus.FOUND);
        response.setLocation(Location.from("/index.html"));
        session.setAttribute("user", loginRequest.getAccount());
        sessionManager.add(session);
        response.addCookie(Cookies.ofJSessionId(session.getId()));
        return null;
    }

    @Override
    public boolean support(final HttpRequest request) {
        final RequestLine requestLine = request.getRequestLine();
        return requestLine.getRequestURI().contains("login") && (requestLine.getHttpMethod() == HttpMethod.POST);
    }
}
