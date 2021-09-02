package nextstep.jwp.web.exceptionhandler;

import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.http.entity.HttpStatus;

public class UnauthorizedExceptionHandler implements ExceptionHandler {

    @Override
    public void handle(HttpRequest httpRequest, HttpResponse httpResponse) {
        httpResponse.setHttpStatus(HttpStatus.FOUND);
        httpResponse.setLocation("/401.html");
    }
}
