package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.ContentType;
import org.apache.coyote.http11.response.HttpResponseBody;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.ResponseEntity;

public class IndexPageController implements Controller {
    @Override
    public ResponseEntity service(HttpRequest request) {
        String requestURI = request.getHttpRequestStartLine().getPath();

        final var responseBody = "Hello world!";
        return ResponseEntity
                .builder()
                .httpStatus(HttpStatus.OK)
                .contentType(generateContentType(requestURI))
                .responseBody(HttpResponseBody.from(responseBody))
                .build();
    }

    private ContentType generateContentType(String requestURI) {
        if (requestURI.endsWith(".css")) {
            return ContentType.CSS;
        }
        return ContentType.HTML;
    }
}
