package nextstep.jwp.exception.handler;

import nextstep.jwp.http.common.HttpStatusCode;
import nextstep.jwp.http.exception.ExceptionHandler;
import nextstep.jwp.http.message.builder.HttpResponseBuilder;
import nextstep.jwp.http.message.response.HttpResponseMessage;

public class StaticResourceNotFoundExceptionHandler implements ExceptionHandler {

    @Override
    public HttpResponseMessage run(RuntimeException exception) {
        return HttpResponseBuilder.status(HttpStatusCode.NOT_FOUND)
                .build();
    }
}
