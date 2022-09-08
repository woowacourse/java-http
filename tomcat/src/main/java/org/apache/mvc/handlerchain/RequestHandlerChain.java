package org.apache.mvc.handlerchain;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public interface RequestHandlerChain {
    HttpResponse handle(HttpRequest request, HttpResponse response);
}
