package nextstep.jwp.presentation.controller;

import nextstep.jwp.application.MemberService;
import nextstep.jwp.dto.request.RegisterRequest;
import nextstep.jwp.presentation.FormDataResolver;
import org.apache.coyote.http11.Http11Request;
import org.apache.coyote.http11.Http11Response;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.RequestLine;
import org.apache.coyote.http11.util.HttpStatus;

public class RegisterRequestHandler implements RequestHandler {

    private final MemberService memberService;

    public RegisterRequestHandler() {
        this.memberService = new MemberService();
    }

    @Override
    public String handle(final Http11Request request, final Http11Response response) {
        memberService.register(RegisterRequest.from(FormDataResolver.resolve(request.getRequestBody())));
        response.setStatusCode(HttpStatus.FOUND.getValue());
        response.setLocation("index");
        return null;
    }

    @Override
    public boolean support(final Http11Request request) {
        final RequestLine requestLine = request.getRequestLine();
        return requestLine.getRequestURI().contains("register") && (requestLine.getHttpMethod() == HttpMethod.POST);
    }
}
