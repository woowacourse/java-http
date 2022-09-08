package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.http.ContentType;
import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.http11.http.HttpStatus;
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

        return new ResponseEntity(HttpStatus.NOTFOUND, FileUtil.readAllBytes("/404.html"), ContentType.HTML);
    }
}
