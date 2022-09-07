package org.apache.coyote.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import nextstep.jwp.exception.NotFoundException;
import org.apache.coyote.exception.InternalServerException;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.Response;

public class ExceptionHandler {

    public static void handle(final Exception exception, final Response response) throws IOException, URISyntaxException {
        if (exception instanceof InternalServerException) {
            response.write(HttpStatus.FOUND, "/404.html");
            return;
        }
        if (exception instanceof NotFoundException) {
            response.write(HttpStatus.FOUND, "/404.html");
            return;
        }
    }
}
