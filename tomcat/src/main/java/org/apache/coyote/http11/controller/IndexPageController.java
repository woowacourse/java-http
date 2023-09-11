package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.*;

import java.io.IOException;

public class IndexPageController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws IOException {
        final var responseBody = "Hello world!";
        ResponseEntity responseEntity = generateResponseEntity(responseBody);
        response.modifyResponse(responseEntity);
    }

    private static ResponseEntity generateResponseEntity(String responseBody) {
        return ResponseEntity
                .builder()
                .httpStatus(HttpStatus.OK)
                .contentType(ContentType.HTML)
                .responseBody(HttpResponseBody.from(responseBody))
                .build();
    }
}
