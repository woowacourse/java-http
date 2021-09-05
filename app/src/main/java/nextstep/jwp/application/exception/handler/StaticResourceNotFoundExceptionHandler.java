package nextstep.jwp.application.exception.handler;

import nextstep.jwp.framework.common.HttpStatusCode;
import nextstep.jwp.framework.exception.ExceptionHandler;
import nextstep.jwp.framework.message.builder.HttpResponseBuilder;
import nextstep.jwp.framework.message.response.HttpResponseMessage;

public class StaticResourceNotFoundExceptionHandler implements ExceptionHandler {

    @Override
    public HttpResponseMessage run(RuntimeException exception) {
        return HttpResponseBuilder.status(HttpStatusCode.NOT_FOUND)
                .build();
    }
}
