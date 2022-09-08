package nextstep.jwp.exception.handler;

import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;

public class MethodNotAllowedHandler implements ExceptionHandler{

    @Override
    public HttpResponse handle() {
        return new HttpResponse.Builder()
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .build();
    }
}
