package org.apache.coyote.http11.controller;

import common.http.Controller;
import common.http.HttpStatus;
import common.http.Request;
import common.http.Response;

import java.util.Arrays;

public class ExceptionController implements Controller {

    @Override
    public void service(Request request, Response response) {
        String exception = response.getException();
        HttpStatus status = ExceptionType.getStatus(exception);
        String pageName = ExceptionType.getExceptionPage(exception);
        response.addHttpStatus(status);
        response.addStaticResourcePath(pageName);
    }

    private enum ExceptionType {
        UNAUTHORIZED(SecurityException.class, HttpStatus.UNAUTHORIZED, "/401.html"),
        BAD_REQUEST(IllegalArgumentException.class, HttpStatus.BAD_REQUEST, "/404.html"),
        CONFLICT(IllegalStateException.class, HttpStatus.CONFLICT, "/409.html"),
        INTERNAL_SERVER_ERROR(Exception.class, HttpStatus.INTERNAL_SERVER_ERROR, "/500.html")
        ;

        private final Class<?> exceptionClass;
        private final HttpStatus httpStatus;
        private final String filePath;

        ExceptionType(Class<?> exceptionClass, HttpStatus httpStatus, String filePath) {
            this.exceptionClass = exceptionClass;
            this.httpStatus = httpStatus;
            this.filePath = filePath;
        }

        public static HttpStatus getStatus(String exception) {
            ExceptionType type = Arrays.stream(ExceptionType.values())
                    .filter(exceptionType -> exceptionType.exceptionClass.getName().equals(exception))
                    .findFirst()
                    .orElse(INTERNAL_SERVER_ERROR);

            return type.httpStatus;
        }

        public static String getExceptionPage(String exception) {
            ExceptionType type = Arrays.stream(ExceptionType.values())
                    .filter(exceptionType -> exceptionType.exceptionClass.getName().equals(exception))
                    .findFirst()
                    .orElse(INTERNAL_SERVER_ERROR);

            return type.filePath;
        }
    }
}
