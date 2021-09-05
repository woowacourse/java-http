package nextstep.jwp.application.exception.handler;

import nextstep.jwp.framework.common.HttpStatusCode;
import nextstep.jwp.framework.exception.ExceptionHandler;
import nextstep.jwp.framework.message.builder.HttpResponseBuilder;
import nextstep.jwp.framework.message.response.HttpResponseMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RuntimeExceptionHandler implements ExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(RuntimeExceptionHandler.class);

    @Override
    public HttpResponseMessage run(RuntimeException exception) {
        log.info("런타임 예외 발생 {}", exception.getMessage());
        return HttpResponseBuilder.status(HttpStatusCode.INTERNAL_SERVER_ERROR)
                .build();
    }
}
