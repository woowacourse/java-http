package org.apache.coyote.handler;

import java.util.Arrays;
import java.util.List;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.exception.InvalidRequestException;
import org.apache.coyote.exception.UserNotFoundException;
import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.response.HttpStatus;
import org.apache.coyote.util.ResourceUtil;

public class ErrorHandler {

    private static final String TEXT_HTML = "text/html";

    public void handle(final Exception exception, final HttpResponse httpResponse) {
        ErrorResource error = ErrorResource.from(exception);
        httpResponse.setHttpStatus(error.getHttpStatus())
                .setContentType(TEXT_HTML)
                .setResponseBody(error.getResource());
    }

    private enum ErrorResource {
        BAD_REQUEST(HttpStatus.BAD_REQUEST, "/401.html", List.of(InvalidRequestException.class)),
        UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "/401.html", List.of(UserNotFoundException.class)),
        NOT_FOUND(HttpStatus.NOT_FOUND, "/404.html", List.of(UncheckedServletException.class)),
        INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "/500.html", List.of()),
        ;

        private final HttpStatus httpStatus;
        private final String resource;
        private final List<Class<? extends Exception>> exceptions;

        ErrorResource(final HttpStatus httpStatus, final String resource,
                      final List<Class<? extends Exception>> exceptions) {
            this.httpStatus = httpStatus;
            this.resource = resource;
            this.exceptions = exceptions;
        }

        public HttpStatus getHttpStatus() {
            return httpStatus;
        }

        public String getResource() {
            return ResourceUtil.getResource(resource);
        }

        public boolean matches(final Exception exception) {
            return exceptions.stream()
                    .map(it -> it.getClass()
                            .getName())
                    .anyMatch(it -> it.equals(exception
                            .getClass()
                            .getName()));
        }

        public static ErrorResource from(final Exception exception) {
            return Arrays.stream(values())
                    .filter(it -> it.matches(exception))
                    .findFirst()
                    .orElse(INTERNAL_SERVER_ERROR);
        }
    }
}
