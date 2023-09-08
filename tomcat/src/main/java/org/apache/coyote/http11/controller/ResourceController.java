package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.common.HttpStatus;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestLine;
import org.apache.coyote.http11.response.ResponseEntity;

public class ResourceController implements Controller {

    private static final ResourceController INSTANCE = new ResourceController();

    private ResourceController() {
    }

    public static ResourceController getInstance() {
        return INSTANCE;
    }

    @Override
    public ResponseEntity service(HttpRequest httpRequest) {
        RequestLine requestLine = httpRequest.getRequestLine();

        return ResponseEntity.of(HttpStatus.OK, requestLine.getPath());
    }

}
