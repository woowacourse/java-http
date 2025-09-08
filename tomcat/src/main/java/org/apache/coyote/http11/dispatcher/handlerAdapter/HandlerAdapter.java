package org.apache.coyote.http11.dispatcher.handlerAdapter;

import org.apache.coyote.http11.request.HttpRequest;

public interface HandlerAdapter {

    boolean canHandle(HttpRequest httpRequest);

    Object handle(HttpRequest httpRequest);
}
