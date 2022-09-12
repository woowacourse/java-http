package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.http.ContentType;
import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.http11.http.HttpStatus;
import org.apache.coyote.util.FileUtil;

public abstract class AbstractRequestHandler implements RequestHandler {

    @Override
    public ResponseEntity handle(HttpRequest httpRequest) {
        if (httpRequest.isGet()) {
            return doGet(httpRequest);
        }
        if (httpRequest.isPost()) {
            return doPost(httpRequest);
        }
        return new ResponseEntity(HttpStatus.NOTFOUND, FileUtil.readAllBytes("/404.html"), ContentType.HTML);
    }

    protected ResponseEntity doPost(HttpRequest httpRequest) {
        throw new IllegalArgumentException();
    }

    protected ResponseEntity doGet(HttpRequest httpRequest) {
        throw new IllegalArgumentException();
    }
}
