package org.apache.coyote.http11.config;

import java.util.List;
import org.apache.catalina.controller.RequestMapping;
import org.apache.catalina.dispatcher.ControllerHandler;
import org.apache.catalina.dispatcher.RequestHandler;
import org.apache.catalina.dispatcher.StaticResourceHandler;

public class HandlerDispatcherConfig {

    private static final List<RequestHandler> requestHandlers = List.of(
            new StaticResourceHandler(),
            new ControllerHandler(new RequestMapping())
    );

    public static List<RequestHandler> getRequestHandlers() {
        return requestHandlers;
    }
}
