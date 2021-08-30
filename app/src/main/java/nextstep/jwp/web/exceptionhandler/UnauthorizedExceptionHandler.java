package nextstep.jwp.web.exceptionhandler;

import nextstep.jwp.http.HttpResponse;

public class UnauthorizedExceptionHandler implements ExceptionHandler {
    @Override
    public String handle(Exception exception) {
        return HttpResponse.unauthorized();
    }
}
