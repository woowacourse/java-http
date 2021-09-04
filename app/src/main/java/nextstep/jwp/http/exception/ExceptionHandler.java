package nextstep.jwp.http.exception;

import nextstep.jwp.http.message.response.HttpResponseMessage;

@FunctionalInterface
public interface ExceptionHandler {
    HttpResponseMessage run(RuntimeException exception);
}
