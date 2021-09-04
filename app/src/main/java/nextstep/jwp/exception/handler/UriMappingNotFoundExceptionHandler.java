package nextstep.jwp.exception.handler;

import nextstep.jwp.http.exception.ExceptionHandler;
import nextstep.jwp.http.message.builder.HttpResponseBuilder;
import nextstep.jwp.http.message.response.HttpResponseMessage;

public class UriMappingNotFoundExceptionHandler implements ExceptionHandler {

    @Override
    public HttpResponseMessage run(RuntimeException exception) {
        return HttpResponseBuilder.redirectTemporarily("/404.html")
                .build();
    }
}
