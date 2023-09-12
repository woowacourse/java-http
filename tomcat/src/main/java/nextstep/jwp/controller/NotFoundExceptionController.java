package nextstep.jwp.controller;

import nextstep.jwp.Controller;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.header.HttpStatus;

public class NotFoundExceptionController extends ExceptionController {
    public static final NotFoundExceptionController INSTANCE = new NotFoundExceptionController();

    @Override
    protected void createErrorResponse(final HttpResponse response) {
        response.addStatus(HttpStatus.FOUND)
                .addLocation("/404.html");
    }

    public static Controller getInstance() {
        return INSTANCE;
    }
}
