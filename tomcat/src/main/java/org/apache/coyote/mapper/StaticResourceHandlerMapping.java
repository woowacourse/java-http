package org.apache.coyote.mapper;

import org.apache.coyote.request.HttpRequest;
import org.apache.handler.Handler;
import org.apache.handler.StaticResourceHandler;

public class StaticResourceHandlerMapping implements HandlerMapping {

    @Override
    public Handler getHandler(HttpRequest request) {
        return new StaticResourceHandler();
    }
}
