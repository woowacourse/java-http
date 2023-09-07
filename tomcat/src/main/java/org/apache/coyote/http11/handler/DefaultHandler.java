package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.common.HttpStatus;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestLine;
import org.apache.coyote.http11.response.ResponseEntity;

public class DefaultHandler implements HttpHandler {
    @Override
    public boolean canHandle(HttpRequest httpRequest) {
        RequestLine requestLine = httpRequest.getRequestLine();
        String path = requestLine.getPath();

        return "/".equals(path);
    }

    @Override
    public ResponseEntity handle(HttpRequest httpRequest) {
        return ResponseEntity.of(HttpStatus.OK, "/default");
    }

}
