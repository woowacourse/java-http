package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseEntity;
import org.apache.coyote.http11.response.statusLine.HttpStatus;

public class FileController implements Controller {

    @Override
    public void service(final HttpRequest request, final HttpResponse response) {
        final ResponseEntity responseEntity = ResponseEntity.of(HttpStatus.OK, request.getResourcePath()
                                                                                      .getResourcePath());

        response.setResponse(HttpResponse.of(request.getHttpVersion(), responseEntity));
    }

    @Override
    public boolean support(final HttpRequest request) {
        return true;
    }
}
