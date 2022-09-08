package nextstep.jwp.presentation.controller;

import nextstep.jwp.application.MemberService;
import nextstep.jwp.dto.request.LoginRequest;
import nextstep.jwp.presentation.FormDataResolver;
import org.apache.catalina.Cookies;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.Http11Request;
import org.apache.coyote.http11.Http11Response;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.RequestLine;
import org.apache.coyote.http11.util.HttpStatus;

public class LoginRequestHandler implements RequestHandler {

    private final MemberService memberService;
    private final SessionManager sessionManager;

    public LoginRequestHandler() {
        this.memberService = new MemberService();
        this.sessionManager = new SessionManager();
    }

    @Override
    public String handle(final Http11Request request, final Http11Response response) {
        if (request.hasCookieByJSessionId()) {
            final Session session = request.getSession();
            sessionManager.findSession(session.getId());
            response.setStatusCode(HttpStatus.FOUND);
            response.setLocation("index");
            return null;
        }
        final String requestBody = request.getRequestBody();
        final LoginRequest loginRequest = LoginRequest.from(FormDataResolver.resolve(requestBody));
        memberService.login(loginRequest);
        response.setStatusCode(HttpStatus.FOUND);
        response.setLocation("index");
        final var session = request.getSession();
        session.setAttribute("user", loginRequest.getAccount());
        sessionManager.add(session);
        response.addCookie(Cookies.ofJSessionId(session.getId()));
        return null;
    }

    @Override
    public boolean support(final Http11Request request) {
        final RequestLine requestLine = request.getRequestLine();
        return requestLine.getRequestURI().contains("login") && (requestLine.getHttpMethod() == HttpMethod.POST);
    }
}
