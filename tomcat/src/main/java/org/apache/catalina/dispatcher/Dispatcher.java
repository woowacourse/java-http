package org.apache.catalina.dispatcher;

import org.apache.catalina.dispatcher.handler.Handler;
import org.apache.catalina.dispatcher.handler.HandlerMapping;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.line.HttpStatus;

import java.io.IOException;
import java.util.Optional;

public class Dispatcher {

    private static final String NOT_FOUND_VIEW = "/404.html";

    private final HandlerMapping handlerMapping;
    private final ViewResolver viewResolver;

    public Dispatcher(HandlerMapping handlerMapping, ViewResolver viewResolver) {
        this.handlerMapping = handlerMapping;
        this.viewResolver = viewResolver;
    }

    public void dispatch(HttpRequest request, HttpResponse response) throws IOException {
        Optional<Handler> handler = handlerMapping.findHandler(request);
        if (handler.isEmpty()) {
            response.setStatus(HttpStatus.NOT_FOUND);
            viewResolver.resolve(NOT_FOUND_VIEW, response);
            return;
        }

        String viewName = handler.get().handle(request, response);
        viewResolver.resolve(viewName, response);
    }

}
