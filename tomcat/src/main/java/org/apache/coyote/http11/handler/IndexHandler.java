package org.apache.coyote.http11.handler;

import org.apache.coyote.Handler;
import org.apache.coyote.common.HttpContentType;
import org.apache.coyote.common.HttpProtocol;
import org.apache.coyote.common.HttpRequest;
import org.apache.coyote.common.HttpResponse;
import org.apache.coyote.common.HttpStatus;

public class IndexHandler implements Handler {

    @Override
    public HttpResponse handle(HttpRequest request) {
        HttpResponse response = new HttpResponse(HttpProtocol.HTTP11, HttpStatus.OK);
        response.setContentType(HttpContentType.TEXT_PLAIN);
        response.setContentBody("Hello world!");
        return response;
    }
}
