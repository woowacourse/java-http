package org.apache.coyote.handler;

import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.view.StaticResourceResolver;
import org.apache.coyote.view.ViewResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ErrorHandler {

    private static final Logger log = LoggerFactory.getLogger(ErrorHandler.class);

    public void handle(HttpResponse response, Exception exception) {
        ViewResolver staticResourceResolver = new StaticResourceResolver();
        if (response.getHttpStatus() == HttpStatus.NOT_FOUND) {
            log.warn("Not Found 에러 발생 | detail : {}", exception.getMessage());
            staticResourceResolver.resolve("400.html", response);
            return;
        }
        if (response.getHttpStatus() == HttpStatus.UNAUTHORIZED) {
            log.warn("Unauthorized 에러 발생 | detail : {}", exception.getMessage());
            staticResourceResolver.resolve("401.html", response);
            return;
        }
        if (response.getHttpStatus() == HttpStatus.BAD_REQUEST) {
            log.warn("Bad Request 에러 발생 | detail : {}", exception.getMessage());
            staticResourceResolver.resolve("404.html", response);
            return;
        }
        if (response.getHttpStatus() == HttpStatus.INTERNAL_SERVER_ERROR) {
            log.error("서버 내부 에러 발생 | detail : {}", exception.getMessage());
            staticResourceResolver.resolve("500.html", response);
        }
    }
}
