package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.ContentType;
import org.apache.coyote.http11.response.HttpResponseBody;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.ResponseEntity;

public class IndexPageController implements Controller {

    @Override
    public ResponseEntity service(HttpRequest request) {
        final var responseBody = "Hello world!";
        return ResponseEntity
                .builder()
                .httpStatus(HttpStatus.OK)
                .contentType(ContentType.HTML)
                .responseBody(HttpResponseBody.from(responseBody))
                .build();
    }
}
