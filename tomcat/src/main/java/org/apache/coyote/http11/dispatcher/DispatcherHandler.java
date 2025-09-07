package org.apache.coyote.http11.dispatcher;

import java.util.List;
import org.apache.coyote.http11.dispatcher.handlerAdapter.HandlerAdapter;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseEntity;

public class DispatcherHandler {

    private final List<HandlerAdapter> adapters;

    public DispatcherHandler(List<HandlerAdapter> adapters) {
        this.adapters = adapters;
    }

    public HttpResponse doService(HttpRequest httpRequest) {
        for (HandlerAdapter handlerAdapter : adapters) {
            if (handlerAdapter.canHandle(httpRequest)) {
                return handlerAdapter.handle(httpRequest);
            }
        }

        return ResponseEntity.notFound();
    }
}
