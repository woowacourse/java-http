package org.apache.coyote.http11.dispatcher.handlerAdapter;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public interface HandlerAdapter {

    boolean supports(Object handler);

    HttpResponse handle(HttpRequest httpRequest, Object handler) throws Exception;
}
