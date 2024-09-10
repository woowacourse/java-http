package org.apache.coyote.controller;

import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.util.ViewResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ErrorController {

    private static final Logger log = LoggerFactory.getLogger(ErrorController.class);

    public void handle(HttpResponse response, Exception exception) {
        if (response.getHttpStatus() == HttpStatus.NOT_FOUND) {
            log.warn("Not Found 에러 발생 | detail : {}", exception.getMessage());
            ViewResolver.resolveView("400.html", response);
            return;
        }
        if (response.getHttpStatus() == HttpStatus.UNAUTHORIZED) {
            log.warn("Unauthorized 에러 발생 | detail : {}", exception.getMessage());
            ViewResolver.resolveView("401.html", response);
            return;
        }
        if (response.getHttpStatus() == HttpStatus.BAD_REQUEST) {
            log.warn("Bad Request 에러 발생 | detail : {}", exception.getMessage());
            ViewResolver.resolveView("404.html", response);
            return;
        }
        if (response.getHttpStatus() == HttpStatus.INTERNAL_SERVER_ERROR) {
            log.error("서버 내부 에러 발생 | detail : {}", exception.getMessage());
            ViewResolver.resolveView("500.html", response);
        }
    }
}
