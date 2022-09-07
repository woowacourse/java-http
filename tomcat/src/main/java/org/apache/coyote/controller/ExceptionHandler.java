package org.apache.coyote.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import nextstep.Application;
import nextstep.jwp.exception.NotFoundException;
import org.apache.coyote.exception.InternalServerException;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static void handle(final Exception exception, final Response response) throws IOException, URISyntaxException {
        log.error(exception.getMessage(), exception);
        if (exception instanceof InternalServerException) {
            response.addHeader("Location", "/500.html");
            response.write(HttpStatus.FOUND);
            return;
        }
        if (exception instanceof NotFoundException) {
            response.addHeader("Location", "/404.html");
            response.write(HttpStatus.FOUND);
        }
    }
}
