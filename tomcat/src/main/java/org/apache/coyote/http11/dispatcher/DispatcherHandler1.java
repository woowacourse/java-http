package org.apache.coyote.http11.dispatcher;

import org.apache.coyote.http11.dispatcher.handlerAdapter.ControllerHandlerAdapter;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseEntity;

public class DispatcherHandler1 {

    private final ControllerHandlerAdapter controllerHandlerAdapter;
    private final HandlerMapping handlerMapping;

    public DispatcherHandler1(ControllerHandlerAdapter controllerHandlerAdapter, HandlerMapping handlerMapping) {
        this.controllerHandlerAdapter = controllerHandlerAdapter;
        this.handlerMapping = handlerMapping;
    }

    public HttpResponse doService(HttpRequest httpRequest) throws Exception {
        Object mappingHandler = handlerMapping.getHandler(httpRequest);
        if (controllerHandlerAdapter.supports(mappingHandler)) {
            return controllerHandlerAdapter.handle(httpRequest, mappingHandler);
        }

        return ResponseEntity.notFound();
    }
}
