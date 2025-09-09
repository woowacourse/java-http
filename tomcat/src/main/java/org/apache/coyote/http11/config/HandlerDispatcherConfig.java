package org.apache.coyote.http11.config;

import java.util.List;
import org.apache.coyote.http11.controller.RequestMapping;
import org.apache.coyote.http11.dispatcher.ControllerAdapter;
import org.apache.coyote.http11.dispatcher.RequestHandler;
import org.apache.coyote.http11.dispatcher.StaticResourceHandler;

public class HandlerDispatcherConfig {

    private static final List<RequestHandler> requestHandlers = List.of(
            new StaticResourceHandler(),
            new ControllerAdapter(new RequestMapping())
    );

    public static List<RequestHandler> getRequestHandlers() {
        return requestHandlers;
    }
}
