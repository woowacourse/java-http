package org.apache.coyote.http11.handler;

import org.apache.coyote.AbstractHandler;
import org.apache.coyote.common.HttpContentType;
import org.apache.coyote.common.HttpRequest;
import org.apache.coyote.common.HttpResponse;
import org.apache.coyote.common.HttpStatus;

public class IndexHandler extends AbstractHandler {

    @Override
    public void handle(HttpRequest request, HttpResponse response) {
        response.setContentType(HttpContentType.TEXT_PLAIN);
        response.setContentBody("Hello world!");
        response.setHttpStatus(HttpStatus.OK);
    }
}
