package org.apache.coyote.http11.advice;

import nextstep.jwp.exception.BadRequestException;
import nextstep.jwp.exception.DuplicatedAccountException;
import nextstep.jwp.exception.InvalidEmailFormException;
import nextstep.jwp.exception.NotFoundException;
import nextstep.jwp.exception.UnAuthorizedException;
import nextstep.jwp.exception.UnsupportedMethodException;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.header.Status;

public class ControllerAdvice {

    public static HttpResponse handleException(Exception exception) throws Exception {
        if (exception instanceof NotFoundException) {
            return HttpResponse.withResource(Status.NOT_FOUND, "/404.html");
        }

        if (exception instanceof UnsupportedMethodException) {
            return HttpResponse.withResource(Status.UNSUPPORTED_METHOD, "/405.html");
        }

        if (exception instanceof BadRequestException) {
            return HttpResponse.withResource(Status.BAD_REQUEST, "/index.html");
        }

        if (exception instanceof UnAuthorizedException) {
            return HttpResponse.withResource(Status.UNAUTHORIZED, "/401.html");
        }

        if (exception instanceof DuplicatedAccountException) {
            return HttpResponse.withResource(Status.BAD_REQUEST, "/400.html");
        }

        if (exception instanceof InvalidEmailFormException) {
            return HttpResponse.withResource(Status.BAD_REQUEST, "/400.html");
        }

        return HttpResponse.withResource(Status.INTERNAL_SERVER_ERROR, "/500.html");
    }
}
