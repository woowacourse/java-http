package org.apache.mvc.handlerchain;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;

public class NotFoundHandlerChain implements RequestHandlerChain {

    @Override
    public HttpResponse handle(HttpRequest ignored, HttpResponse response) {
        return response.update(HttpStatus.NOT_FOUND, "");
    }
}
