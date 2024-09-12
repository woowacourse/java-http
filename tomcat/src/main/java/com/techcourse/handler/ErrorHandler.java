package com.techcourse.handler;

import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.response.HttpResponse;
import com.techcourse.servlet.view.StaticResourceView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ErrorHandler {

    private static final Logger log = LoggerFactory.getLogger(ErrorHandler.class);

    public void handle(HttpResponse response, Exception exception) {
        if (response.getHttpStatus() == HttpStatus.NOT_FOUND) {
            log.warn("Not Found 에러 발생 | detail : {}", exception.getMessage());
            new StaticResourceView("400.html").render(response);
            return;
        }
        if (response.getHttpStatus() == HttpStatus.UNAUTHORIZED) {
            log.warn("Unauthorized 에러 발생 | detail : {}", exception.getMessage());
            new StaticResourceView("401.html").render(response);
            return;
        }
        if (response.getHttpStatus() == HttpStatus.BAD_REQUEST) {
            log.warn("Bad Request 에러 발생 | detail : {}", exception.getMessage());
            new StaticResourceView("404.html").render(response);
            return;
        }
        if (response.getHttpStatus() == HttpStatus.INTERNAL_SERVER_ERROR) {
            log.error("서버 내부 에러 발생 | detail : {}", exception.getMessage());
            new StaticResourceView("500.html").render(response);
        }
    }
}
