package org.apache.coyote.http11.exception;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;

import com.techcourse.exception.UnauthorizedException;

public class UnauthorizedExceptionHandler implements ExceptionHandler {

    @Override
    public boolean support(Exception e) {
        return e instanceof UnauthorizedException;
    }

    @Override
        public void handle(HttpRequest request, HttpResponse response, Exception e) {
            request.setPath("/401.html");
            response.setStatus(HttpStatus.UNAUTHORIZED);
            response.getHeaders().clear();
    }
}
