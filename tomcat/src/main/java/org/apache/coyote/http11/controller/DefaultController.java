package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.ResponseEntity;

public class DefaultController implements Controller {

    private static final DefaultController INSTANCE = new DefaultController();

    private DefaultController() {
    }

    public static DefaultController getInstance() {
        return INSTANCE;
    }

    @Override
    public ResponseEntity service(HttpRequest httpRequest) {
        return ResponseEntity.ok()
                .path("/default")
                .build();
    }

}
