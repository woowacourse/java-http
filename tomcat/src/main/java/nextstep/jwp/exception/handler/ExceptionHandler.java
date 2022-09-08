package nextstep.jwp.exception.handler;

import org.apache.coyote.http11.response.HttpResponse;

public interface ExceptionHandler {

    HttpResponse handle();
}
