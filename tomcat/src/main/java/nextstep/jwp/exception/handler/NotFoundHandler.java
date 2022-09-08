package nextstep.jwp.exception.handler;

import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;

public class NotFoundHandler implements ExceptionHandler {

    private static final String PATH = "/404.html";

    @Override
    public HttpResponse handle() {
        return new HttpResponse.Builder()
                .status(HttpStatus.FOUND)
                .location(PATH)
                .build();
    }
}
