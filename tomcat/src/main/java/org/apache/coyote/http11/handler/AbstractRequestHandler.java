package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.ResponseEntity;
import org.apache.coyote.http11.message.HttpRequest;
import org.apache.coyote.http11.message.HttpStatus;
import org.apache.coyote.util.FileUtil;

public abstract class AbstractRequestHandler implements RequestHandler {

    @Override
    public ResponseEntity handle(HttpRequest httpRequest) {
        if (httpRequest.getHttpMethod().isGet()) {
            return doGet(httpRequest);
        }
        if (httpRequest.getHttpMethod().isPost()) {
            return doPost(httpRequest);
        }
        return new ResponseEntity(HttpStatus.NOTFOUND, FileUtil.readAllBytes("/404.html"), ContentType.HTML);
    }

    private ResponseEntity doPost(HttpRequest httpRequest) {
        return null;
    }

    private ResponseEntity doGet(HttpRequest httpRequest) {
        return null;
    }
}
