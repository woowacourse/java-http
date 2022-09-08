package nextstep.jwp.exception.handler;

import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;

public class RuntimeExceptionHandler implements ExceptionHandler {

    private static final String PATH = "/500.html";

    @Override
    public HttpResponse handle() {
        return new HttpResponse.Builder()
                .status(HttpStatus.FOUND)
                .location(PATH)
                .build();
    }
}
