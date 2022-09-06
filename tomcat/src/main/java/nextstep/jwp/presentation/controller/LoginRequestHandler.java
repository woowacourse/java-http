package nextstep.jwp.presentation.controller;

import nextstep.jwp.application.MemberService;
import nextstep.jwp.dto.request.LoginRequest;
import org.apache.coyote.http11.Request;
import org.apache.coyote.http11.Response;
import org.apache.coyote.http11.util.HttpStatus;

public class LoginRequestHandler implements RequestHandler {

    private final MemberService memberService;

    public LoginRequestHandler() {
        this.memberService = new MemberService();
    }

    @Override
    public String handle(final Request request, final Response response) {
        memberService.login(LoginRequest.from(request.getQueryParams()));
        response.setStatusCode(HttpStatus.FOUND.getValue());
        response.setLocation("index");
        return null;
    }

    @Override
    public boolean support(final Request request) {
        return request.getRequestURI().contains("login") && !request.getQueryParams().isEmpty();
    }
}
