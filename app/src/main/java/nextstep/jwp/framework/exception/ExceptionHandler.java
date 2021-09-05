package nextstep.jwp.framework.exception;

import nextstep.jwp.framework.message.response.HttpResponseMessage;

@FunctionalInterface
public interface ExceptionHandler {
    HttpResponseMessage run(RuntimeException exception);
}
