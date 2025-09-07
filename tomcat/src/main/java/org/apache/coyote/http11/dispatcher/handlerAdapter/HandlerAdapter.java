package org.apache.coyote.http11.dispatcher.handlerAdapter;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public interface HandlerAdapter {

    boolean canHandle(HttpRequest httpRequest);

    HttpResponse handle(HttpRequest httpRequest);
}
