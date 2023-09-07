package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.common.HttpStatus;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.ResponseEntity;

public class NotFoundHandler implements HttpHandler {

    @Override
    public boolean canHandle(HttpRequest httpRequest) {
        return false;
    }

    @Override
    public ResponseEntity handle(HttpRequest httpRequest) {
        return ResponseEntity.of(HttpStatus.NOT_FOUND, "/404");
    }

}
