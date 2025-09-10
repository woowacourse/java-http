package org.apache.coyote.http11.exception;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;

public class IllegalArgumentExceptionHandler implements ExceptionHandler {

    @Override
    public boolean support(Exception e) {
        return e instanceof IllegalArgumentException;
    }

    @Override
        public void handle(HttpRequest request, HttpResponse response, Exception e) {
            request.setPath("/404.html");
            response.setStatus(HttpStatus.NOT_FOUND);
            response.getHeaders().clear();
    }
}
