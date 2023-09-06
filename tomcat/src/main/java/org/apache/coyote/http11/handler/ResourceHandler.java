package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.common.HttpStatus;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestLine;
import org.apache.coyote.http11.response.ResponseEntity;

public class ResourceHandler implements HttpHandler {

    @Override
    public boolean canHandle(HttpRequest httpRequest) {
        return httpRequest.isStaticResource();
    }

    @Override
    public ResponseEntity handle(HttpRequest httpRequest) {
        RequestLine requestLine = httpRequest.getRequestLine();

        return new ResponseEntity(HttpStatus.OK, requestLine.getPath());
    }

}
