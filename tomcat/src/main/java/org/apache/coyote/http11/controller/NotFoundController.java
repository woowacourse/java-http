package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.ResponseEntity;

public class NotFoundController implements Controller {

    private static final NotFoundController INSTANCE = new NotFoundController();

    private NotFoundController() {
    }

    public static NotFoundController getInstance() {
        return INSTANCE;
    }

    @Override
    public ResponseEntity service(HttpRequest httpRequest) {
        return ResponseEntity.notFound()
                .path("/404")
                .build();
    }

}
