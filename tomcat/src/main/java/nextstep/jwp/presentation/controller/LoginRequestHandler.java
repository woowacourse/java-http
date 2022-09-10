package nextstep.jwp.presentation.controller;

import static nextstep.jwp.presentation.ResourceLocation.ROOT;

import customservlet.RequestHandler;
import customservlet.SessionManager;
import nextstep.jwp.application.MemberService;
import nextstep.jwp.dto.request.LoginRequest;
import nextstep.jwp.presentation.resolver.FormDataResolver;
import org.apache.coyote.http11.http.Cookies;
import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.http11.http.HttpResponse;
import org.apache.coyote.http11.http.RequestLine;
import org.apache.coyote.http11.util.HttpMethod;
import org.apache.coyote.http11.util.HttpStatus;

public class LoginRequestHandler implements RequestHandler {

    private final MemberService memberService;
    private final SessionManager sessionManager;

    public LoginRequestHandler() {
        this.memberService = new MemberService();
        this.sessionManager = SessionManager.getInstance();
    }

    @Override
    public String handle(final HttpRequest request, final HttpResponse response) {
        final var loginRequest = LoginRequest.from(FormDataResolver.resolve(request.getRequestBody()));
        memberService.login(loginRequest);

        final var session = request.getSession(true);
        session.setAttribute("user", loginRequest.getAccount());
        sessionManager.add(session);
        response.addCookie(Cookies.ofJSessionId(session.getId()));
        response.setStatusCode(HttpStatus.FOUND);
        response.setLocation(ROOT.getLocation());
        return null;
    }

    @Override
    public boolean support(final HttpRequest request) {
        final RequestLine requestLine = request.getRequestLine();
        return requestLine.getRequestUri().containUrl("login") && (requestLine.getHttpMethod() == HttpMethod.POST);
    }
}
