package org.apache.mvc;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.mvc.handlerchain.RequestHandlerChain;

public class HandlerMapper {

    private final RequestHandlerChain chain;

    public HandlerMapper(RequestHandlerChain chain) {
        this.chain = chain;
    }

    public HttpResponse mapToHandle(HttpRequest request) {
        return chain.handle(request);
    }
}
