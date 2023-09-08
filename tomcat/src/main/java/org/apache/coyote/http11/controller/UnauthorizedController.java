package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.common.HttpStatus;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.ResponseEntity;

public class UnauthorizedController implements Controller {

    private static final UnauthorizedController INSTANCE = new UnauthorizedController();

    private UnauthorizedController() {
    }

    public static UnauthorizedController getInstance() {
        return INSTANCE;
    }

    @Override
    public ResponseEntity service(HttpRequest httpRequest) {
        return ResponseEntity.of(HttpStatus.UNAUTHORIZED, "/401");
    }
}
