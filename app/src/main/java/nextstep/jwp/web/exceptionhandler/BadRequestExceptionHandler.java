package nextstep.jwp.web.exceptionhandler;

import nextstep.jwp.http.HttpResponse;

public class BadRequestExceptionHandler implements ExceptionHandler {
    @Override
    public String handle(Exception exception) {
        return HttpResponse.badRequest();
    }
}
