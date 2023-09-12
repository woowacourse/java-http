package nextstep.jwp.controller;

import nextstep.jwp.Controller;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.header.HttpStatus;

public class AuthenticationExceptionController extends ExceptionController {
    public static final AuthenticationExceptionController INSTANCE = new AuthenticationExceptionController();

    @Override
    protected void createErrorResponse(final HttpResponse response) {
        response.addStatus(HttpStatus.FOUND)
                .addLocation("/login.html");
    }

    public static Controller getInstance() {
        return INSTANCE;
    }
}
