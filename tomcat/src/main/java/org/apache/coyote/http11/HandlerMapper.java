package org.apache.coyote.http11;

import java.util.List;
import org.apache.coyote.http11.handler.Handler;
import org.apache.coyote.http11.handler.LoginHandler;
import org.apache.coyote.http11.handler.RootHandler;
import org.apache.coyote.http11.handler.StaticResourceHandler;

public class HandlerMapper {

    private static final List<Handler> HANDLERS = List.of(new RootHandler(), new StaticResourceHandler(),
            new LoginHandler());

    public Handler find(HttpRequest httpRequest) {
        for (Handler handler : HANDLERS) {
            if (handler.supports(httpRequest)) {
                return handler;
            }
        }
        throw new RuntimeException();
    }
}
