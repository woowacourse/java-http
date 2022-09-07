package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.ResponseEntity;
import org.apache.coyote.http11.message.HttpRequest;
import org.apache.coyote.http11.message.HttpStatus;
import org.apache.coyote.util.FileUtil;

public class FrontRequestHandler {

    private final RequestHandlerMapping requestHandlerMapping;

    public FrontRequestHandler() {
        this.requestHandlerMapping = RequestHandlerMapping.init();
    }

    public ResponseEntity handle(HttpRequest httpRequest) {
        if (httpRequest.isRoot()) {
            return new ResponseEntity(HttpStatus.OK, "Hello world!", ContentType.HTML);
        }

        if (requestHandlerMapping.acceptHandler(httpRequest.getRequestUri())) {
            RequestHandler handler = requestHandlerMapping.getHandler(httpRequest.getRequestUri());
            return handler.handle(httpRequest);
        }

        if (FileUtil.existResource(httpRequest.getRequestUri())) {
            return FileHandler.handle(httpRequest);
        }

        throw new UnsupportedOperationException();
    }
}
